package burtis.modules.network;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractSocketService implements SocketService
{
    private final Logger logger;
    private final int port;
    private Socket socket;
    private final Lock socketChangingLock = new ReentrantLock();
    private final Lock socketInUseLock = new ReentrantLock();
    private final Lock socketBeingRead = new ReentrantLock();

    public AbstractSocketService(final int port, final Logger logger)
    {
        this.port = port;
        this.logger = logger;
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
            socket = socketFactory();
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

    protected abstract Socket socketFactory() throws IOException;
}
