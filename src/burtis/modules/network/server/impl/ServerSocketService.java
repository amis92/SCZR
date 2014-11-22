package burtis.modules.network.server.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.modules.network.server.Server;
import burtis.modules.network.server.SocketService;

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
public class ServerSocketService implements SocketService
{
    private final Logger logger = Logger.getLogger(Server.class.getName());
    private final int port;
    private Socket socket;
    private final Lock socketChangingLock = new ReentrantLock();
    private final Lock socketInUseLock = new ReentrantLock();
    private final Lock socketBeingRead = new ReentrantLock();

    public ServerSocketService(final int port)
    {
        this.port = port;
    }

    @Override
    public void close()
    {
        if (!socketChangingLock.tryLock())
        {
            return;
        }
        try
        {
            socketInUseLock.lock();
            if (socket != null && !socket.isClosed())
            {
                socket.close();
            }
        }
        catch (final IOException e)
        {
            logger.log(Level.WARNING, "Błąd zamykania połączenia", e);
        }
        finally
        {
            socket = null;
            socketInUseLock.unlock();
            socketChangingLock.unlock();
        }
    }

    @Override
    public void connect() throws IOException
    {
        if (!socketChangingLock.tryLock())
        {
            return;
        }
        try
        {
            socketInUseLock.lock();
            if (isConnected())
            {
                return;
            }
            close();
            final ServerSocket serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            serverSocket.close();
        }
        finally
        {
            socketInUseLock.unlock();
            socketChangingLock.unlock();
        }
    }

    @Override
    public int getPort()
    {
        return port;
    }

    @Override
    public boolean isConnected()
    {
        if (!socketChangingLock.tryLock())
        {
            return false;
        }
        try
        {
            boolean isConnected = socket != null && socket.isConnected()
                    && !socket.isClosed();
            return isConnected;
        }
        finally
        {
            socketChangingLock.unlock();
        }
    }

    @Override
    public void readFromSocket(Consumer<Socket> action)
    {
        socketBeingRead.lock();
        try
        {
            if (socket == null)
            {
                return;
            }
            action.accept(socket);
        }
        finally
        {
            socketBeingRead.unlock();
        }
    }

    @Override
    public void writeToSocket(Consumer<Socket> action)
    {
        socketInUseLock.lock();
        try
        {
            if (socket == null)
            {
                return;
            }
            action.accept(socket);
        }
        finally
        {
            socketInUseLock.unlock();
        }
    }
}
