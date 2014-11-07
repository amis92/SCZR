package burtis.modules.network.client;

import java.util.concurrent.LinkedBlockingQueue;

import burtis.modules.network.Connection;

/**
 * Sends and receives objects to/from server.
 *
 * @author Amadeusz Sadowski
 *
 * @param <T>
 *            Received objects are cast to this type.
 */
public class Client<T> implements Connection
{
    private final ClientConnection<T> connection;
    /**
     * Queue of events received from server.
     */
    private LinkedBlockingQueue<T> eventsBlockingQueue = new LinkedBlockingQueue<T>();

    public Client(final String serverAddress, final int serverPort)
    {
        this.connection = new ClientConnection<T>(serverAddress, serverPort,
                this::receiveObject);
    }

    @Override
    public void closeConnection()
    {
        connection.closeConnection();
    }

    @Override
    public boolean connect()
    {
        return connection.connect();
    }

    public LinkedBlockingQueue<T> getEventsBlockingQueue()
    {
        return eventsBlockingQueue;
    }

    @Override
    public boolean isConnected()
    {
        return connection.isConnected();
    }

    @Override
    public boolean send(Object objectToSend)
    {
        return connection.send(objectToSend);
    }

    public void setEventsBlockingQueue(
            LinkedBlockingQueue<T> eventsBlockingQueue)
    {
        this.eventsBlockingQueue = eventsBlockingQueue;
    }

    private void receiveObject(T receivedObject)
    {
        eventsBlockingQueue.add(receivedObject);
    }
}
