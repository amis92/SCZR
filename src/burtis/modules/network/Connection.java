package burtis.modules.network;

/**
 * Defines necessary methods to manage and use one-way connection. Allows only
 * sending objects. Receiving isn't defined.
 * 
 * @author Amadeusz Sadowski
 *
 */
public interface Connection
{
    public void closeConnection();

    public boolean connect();

    public boolean isConnected();

    /**
     * Sends object through Connection.
     * 
     * @param objectToSend
     *            - object to send.
     * 
     * @return true if transfer succeeded.
     */
    public boolean send(final Object objectToSend);
}
