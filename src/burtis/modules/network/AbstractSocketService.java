package burtis.modules.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractSocketService implements SocketService
{
    private final Logger logger;
    private final int port;
    private final Lock socketBeingRead = new ReentrantLock();
    private final Lock socketChangingLock = new ReentrantLock();
    private SocketChannel socketChannel;
    private final Lock socketInUseLock = new ReentrantLock();

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
            if (socketChannel != null)
            {
                socketChannel.close();
            }
        }
        catch (final IOException e)
        {
            logger.log(Level.WARNING, "Błąd zamykania połączenia", e);
        }
        finally
        {
            socketChannel = null;
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
            if (isConnected())
            {
                return;
            }
            close();
            socketChannel = socketChannelFactory();
        }
        finally
        {
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
            boolean isConnected = socketChannel != null
                    && socketChannel.isConnected();
            return isConnected;
        }
        finally
        {
            socketChangingLock.unlock();
        }
    }

    @Override
    public void readFromSocket(Consumer<Object> receive) throws IOException
    {
        socketBeingRead.lock();
        try
        {
            if (socketChannel == null)
            {
                return;
            }
            ObjectInputStream ois = new ObjectInputStream(getInputStream());
            logger.log(Level.FINEST, "Czekam na obiekt");
            Object object = ois.readObject();
            logger.log(Level.FINEST, "Dostalem obiekt: "
                    + object.getClass().getName());
            receive.accept(object);
        }
        catch (ClassNotFoundException e)
        {
            logger.log(Level.WARNING, "Ignorowanie nieznanej klasy", e);
        }
        finally
        {
            socketBeingRead.unlock();
        }
    }

    @Override
    public void writeToSocket(Object o)
    {
        socketInUseLock.lock();
        try
        {
            if (socketChannel == null)
            {
                return;
            }
            ObjectOutputStream oos = null;
            OutputStream socketOutput = Channels.newOutputStream(socketChannel);
            oos = new ObjectOutputStream(socketOutput);
            oos.writeObject(o);
            oos.flush();
            logger.finest("Wysłałem");
        }
        catch (Exception e)
        {
            logger.log(Level.WARNING, "Błąd wysyłania", e);
        }
        finally
        {
            socketInUseLock.unlock();
        }
    }

    private InputStream getInputStream() throws IOException
    {
        return Channels.newInputStream(new ReadableByteChannel() {
            public void close() throws IOException
            {
                socketChannel.close();
            }

            public boolean isOpen()
            {
                return socketChannel.isOpen();
            }

            public int read(ByteBuffer dst) throws IOException
            {
                return socketChannel.read(dst);
            }
        });
    }

    protected abstract SocketChannel socketChannelFactory() throws IOException;
}
