package burtis.modules.network.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.modules.network.Connection;
import main.SimulatorConstants;

/**
 * Manages connection to specified address/port pair.
 * 
 * Allows for opening, closing and restarting connection. Connection status can
 * be retrieved.
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
    private volatile Lock socketLock = new ReentrantLock();

    public ClientConnection(final String serverAddress, final int serverPort,
            Consumer<T> receiveAction)
    {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.receiveAction = receiveAction;
    }

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
            receiveLoopExecutorService.shutdownNow();
        }
        finally
        {
            socket = new Socket();
            socketLock.unlock();
        }
    }

    public boolean connect()
    {
        socketLock.lock();
        try
        {
            if (!isConnected())
            {
                return false; // only one call should be running at a time
            }
            closeConnection();
            socket.connect(new InetSocketAddress(serverAddress, serverPort),
                    SimulatorConstants.connectingTimeout);
            receiveLoopExecutorService.execute(() -> listenOnSocket());
            return true;
        }
        catch (final Exception e)
        {
            logger.log(Level.FINE, "Błąd łączenia z serwerem");
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
        return true;
    }

    @SuppressWarnings("unchecked")
    private void listenOnSocket()
    {
        logger.log(Level.INFO, "Rozpoczynam nasłuchiwanie w kliencie");
        ObjectInputStream ois = null;
        while (!socket.isConnected())
        {
            socketLock.lock();
            try
            {
                logger.log(Level.INFO, "Czekam " + socket.getLocalPort());
                ois = new ObjectInputStream(socket.getInputStream());
                final Object receivedObject = ois.readObject();
                logger.log(Level.INFO, "Dostalem: " + receivedObject.getClass());
                receiveAction.accept((T) receivedObject);
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
