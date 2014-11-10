package burtis.modules.network.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.SimulatorConstants;
import burtis.modules.network.Connection;

/**
 * Manages connection to specified address/port pair.
 * 
 * Allows for opening, closing and reopening connection. Connection status can
 * be retrieved. Thread-safety: each action on Socket is done in Locked block.
 * 
 * @author Amadeusz Sadowski
 *
 * @param <T>
 *            Received objects are cast to this type, and then consumed by
 *            provided Consumer.
 */
class ClientConnection<T> implements Connection
{
    private final Logger logger = Logger.getLogger(Client.class.getName());
    private final Consumer<T> receiveAction;
    private final ExecutorService receiveLoopExecutorService = Executors
            .newSingleThreadExecutor();
    private final String serverAddress;
    private final int serverPort;
    private Socket socket = new Socket();
    private final Lock socketLock = new ReentrantLock();

    public ClientConnection(final String serverAddress, final int serverPort,
            final Consumer<T> receiveAction)
    {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.receiveAction = receiveAction;
    }

    /**
     * Attempts to close connection and create new empty Socket.
     */
    public void closeConnection()
    {
        socketLock.lock();
        try
        {
            if (!socket.isClosed())
            {
                socket.close();
            }
        }
        catch (final IOException e)
        {
            logger.log(Level.WARNING, "Błąd zamykania połączenia", e);
            receiveLoopExecutorService.shutdownNow();
        }
        finally
        {
            socket = new Socket();
            socketLock.unlock();
        }
    }

    /**
     * Attempts to connect with server. Blocking method. On success listener is
     * subscribed to open connection.
     */
    public boolean connect()
    {
        socketLock.lock();
        try
        {
            if (isConnected())
            {
                return true; // might've connected while thread awaited for lock
            }
            closeConnection();
            SocketAddress address = new InetSocketAddress(serverAddress,
                    serverPort);
            socket.connect(address, SimulatorConstants.connectingTimeout);
            receiveLoopExecutorService.execute(this::listenOnSocket);
            return true;
        }
        catch (final Exception e)
        {
            logger.log(Level.WARNING, "Błąd łączenia z serwerem", e);
            closeConnection();
            return false;
        }
        finally
        {
            socketLock.unlock();
        }
    }

    public boolean isConnected()
    {
        socketLock.lock();
        try
        {
            boolean isConnected = socket.isConnected() && !socket.isClosed();
            return isConnected;
        }
        finally
        {
            socketLock.unlock();
        }
    }

    /**
     * Sends parameter to Socket. Closes connection on failure.
     */
    public boolean send(final Object object)
    {
        if (!isConnected())
        {
            return false;
        }
        socketLock.lock();
        ObjectOutputStream oos = null;
        try
        {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(object);
            oos.flush();
            oos.close();
            return true;
        }
        catch (final IOException e)
        {
            logger.log(Level.WARNING, "Błąd wysyłania do serwera", e);
            closeConnection();
            return false;
        }
        finally
        {
            socketLock.unlock();
        }
    }

    /**
     * Awaits for incoming objects, and calls receive action for them.
     */
    @SuppressWarnings("unchecked")
    private void listenOnSocket()
    {
        logger.log(Level.INFO, "Rozpoczynam nasłuchiwanie w kliencie");
        ObjectInputStream ois = null;
        while (isConnected() && !Thread.interrupted())
        {
            socketLock.lock();
            try
            {
                logger.log(Level.FINER, "Czekam " + socket.getLocalPort());
                ois = new ObjectInputStream(socket.getInputStream());
                final Object receivedObject = ois.readObject();
                logger.log(Level.FINER,
                        "Dostalem: " + receivedObject.getClass());
                receiveAction.accept((T) receivedObject);
                ois.close();
            }
            catch (final IOException e)
            {
                logger.log(Level.SEVERE, "Błąd odbierania z serwera", e);
            }
            catch (final ClassNotFoundException e)
            {
                logger.log(Level.WARNING, "Ignorowanie nieznanej klasy", e);
            }
            finally
            {
                socketLock.unlock();
            }
        }
        logger.log(Level.INFO, "Zakończyłem nasłuchiwanie w kliencie");
    }
}
