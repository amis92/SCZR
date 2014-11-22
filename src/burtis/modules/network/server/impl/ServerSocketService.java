package burtis.modules.network.server.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import burtis.modules.network.AbstractSocketService;
import burtis.modules.network.SocketService;
import burtis.modules.network.server.Server;

/**
 * Methods in this implementation are thread-safe. Each call tries to acquire
 * lock. If it fails, that means another method of this object is currently
 * opening or closing the socket. In that case it will immediately return.
 * {@link #isConnected} doesn't use changing state lock.
 * 
 * {@link SocketService} implementation.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class ServerSocketService extends AbstractSocketService
{
    private final static Logger logger = Logger.getLogger(Server.class
            .getName());

    public ServerSocketService(final int port)
    {
        super(port, logger);
    }

    @Override
    protected Socket socketFactory() throws IOException
    {
        final ServerSocket serverSocket = new ServerSocket(getPort());
        final Socket socket = serverSocket.accept();
        serverSocket.close();
        return socket;
    }
}
