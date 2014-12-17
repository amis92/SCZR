package burtis.modules.network.client;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.modules.network.Listener;
import burtis.modules.network.ListenerImpl;
import burtis.modules.network.SocketService;

/**
 * Sends and receives objects to/from server.
 *
 * @author Amadeusz Sadowski
 *
 * @param <T>
 *            Received objects are cast to this type.
 */
public class ClientConnection<T>
{
    private final static Logger logger = Logger
            .getLogger(ClientConnection.class.getName());
    /**
     * Queue of events received from server.
     */
    protected final LinkedBlockingQueue<T> incomingQueue = new LinkedBlockingQueue<T>();
    private final Listener listener;
    private ExecutorService listenerExecutor = Executors
            .newSingleThreadExecutor();
    private final SocketService socketService;

    public ClientConnection(final String serverAddress, final int serverPort)
    {
        this.socketService = new ClientSocketService(serverAddress, serverPort);
        this.listener = new ListenerImpl(socketService, this::receive,
                this::reconnect, logger);
    }

    public void close()
    {
        logger.info("Zamykam połączenie z serwerem.");
        listenerExecutor.shutdownNow();
        listenerExecutor = Executors.newSingleThreadExecutor();
        socketService.close();
    }

    public void connect() throws IOException
    {
        final int port = socketService.getPort();
        try
        {
            logger.info("Resetuję połączenie.");
            close();
            logger.info("Oczekuję na połączenie z serwerem na porcie " + port);
            socketService.connect();
            listenerExecutor.execute(listener::listen);
            logger.info("Podłączono do serwera na porcie " + port);
        }
        catch (final ClosedByInterruptException e)
        {
            logger.warning("Przerywam łączenie z serwerem.");
        }
        catch (final IOException e)
        {
            logger.severe("Nie udało się połączyć z serwerem.");
            throw e;
        }
    }

    public T takeFromQueue() throws InterruptedException
    {
        return incomingQueue.take();
    }

    public boolean isConnected()
    {
        return socketService.isConnected();
    }

    @SuppressWarnings("unchecked")
    private void receive(Object receivedObject)
    {
        incomingQueue.add((T) receivedObject);
    }

    private void reconnect()
    {
        try
        {
            connect();
        }
        catch (IOException e)
        {
            logger.severe("Klient nie mógł połączyć się ponownie.");
            throw new RuntimeException(e);
        }
    }

    public void send(Object objectToSend)
    {
        try
        {
            socketService.writeToSocket(objectToSend);
        }
        catch (Exception e)
        {
            logger.log(Level.WARNING, "Błąd wysyłania na serwer obiektu "
                    + objectToSend, e);
        }
    }
}
