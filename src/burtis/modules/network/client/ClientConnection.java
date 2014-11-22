package burtis.modules.network.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.modules.network.Listener;
import burtis.modules.network.ListenerImpl;
import burtis.modules.network.server.Server;

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
    private final static Logger logger = Logger.getLogger(Server.class
            .getName());
    /**
     * Queue of events received from server.
     */
    private LinkedBlockingQueue<T> incomingQueue = new LinkedBlockingQueue<T>();
    private final Listener listener;
    private ExecutorService listenerExecutor = Executors
            .newSingleThreadExecutor();
    private final ClientSocketService socketService;

    public ClientConnection(final String serverAddress, final int serverPort)
    {
        this.socketService = new ClientSocketService(serverAddress, serverPort);
        this.listener = new ListenerImpl(socketService, this::receive,
                this::reconnect, logger);
    }

    public void close()
    {
        socketService.close();
    }

    public void connect() throws IOException
    {
        try
        {
            logger.info(String.format(
                    "Oczekuję na połączenie z serwerem na porcie %d",
                    socketService.getPort()));
            listenerExecutor.shutdownNow();
            listenerExecutor = Executors.newSingleThreadExecutor();
            socketService.connect();
            listenerExecutor.execute(listener::listen);
            logger.info(String.format("Podłączono do serwera na porcie %d",
                    socketService.getPort()));
        }
        catch (final IOException e)
        {
            logger.log(Level.SEVERE, "Błąd tworzenia Listener'a", e);
            throw e;
        }
    }

    public LinkedBlockingQueue<T> getIncomingQueue()
    {
        return incomingQueue;
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
        socketService.writeToSocket((socket) ->
        {
            ObjectOutputStream oos = null;
            try
            {
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(objectToSend);
                oos.flush();
                logger.log(Level.FINEST, "Wysłałem");
            }
            catch (Exception e)
            {
                logger.log(Level.WARNING, "Błąd wysyłania", e);
            }
        });
    }
}
