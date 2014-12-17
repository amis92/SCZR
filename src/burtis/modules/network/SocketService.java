package burtis.modules.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.function.Consumer;

/**
 * Groups socket management operations.
 * 
 * @author Amadeusz Sadowski
 *
 */
public interface SocketService
{
    /**
     * Attempts to close socket. Fails silently.
     */
    public void close();

    /**
     * Waits for connection on given port using {@link ServerSocket}. Blocking
     * method. Attempts to close previous socket if any.
     * 
     * @throws IOException
     *             When an error during connection occurs.
     */
    public void connect() throws IOException;

    /**
     * Retrieves local port number to which this socket is/should be bound.
     * 
     * @return Local port being used.
     */
    public int getPort();

    /**
     * Performs check whether socket is open, connected, and wasn't closed yet.
     * 
     * @param socket
     *            Socket to be checked.
     * @return True if connection is online.
     */
    public boolean isConnected();

    /**
     * Allows for thread-safe use of the socket. After acquiring lock on socket,
     * it won't be closed or reopened. The lock is released after method
     * returns. Fails silently.
     * 
     * @param o
     *            Object sent into connection.
     */
    public void writeToSocket(Object o);

    /**
     * Allows for thread-safe reading. This method acquires read-lock, but
     * doesn't lock socket from being closed or written to in the meantime. The
     * only assert it provides is that only one thread may call this method at
     * once.
     * 
     * @param receive
     *            Called with the object received, or not called if error
     *            occurs.
     * @throws IOException
     *             When an IO error occurs.
     */
    public void readFromSocket(Consumer<Object> receive) throws IOException;
}
