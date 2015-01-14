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
        logger.info("Closing connection to server.");
        listenerExecutor.shutdownNow();
        listenerExecutor = Executors.newSingleThreadExecutor();
        socketService.close();
    }

    public void connect() throws IOException
    {
        final int port = socketService.getPort();
        try
        {
            logger.info("Resetting connection.");
            close();
            logger.info("Awaiting connection from server on port " + port);
            socketService.connect();
            listenerExecutor.execute(listener::listen);
            logger.info("Connected to server on port " + port);
        }
        catch (final ClosedByInterruptException e)
        {
            logger.warning("Interrupting connecting with server.");
        }
        catch (final IOException e)
        {
            logger.severe("Connection failed.");
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
            logger.severe("Client couldn't reconnect.");
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
            logger.log(Level.WARNING, "Error sending object "
                    + objectToSend, e);
        }
    }
}
