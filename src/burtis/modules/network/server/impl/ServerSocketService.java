package burtis.modules.network.server.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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
    protected SocketChannel socketChannelFactory() throws IOException
    {
        final ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(getPort()));
        final SocketChannel socket = serverSocket.accept();
        socket.configureBlocking(true);
        serverSocket.close();
        return socket;
    }
}
