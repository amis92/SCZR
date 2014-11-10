package burtis.modules.network.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sends objects using blocking queue on separate thread.
 * 
 * @author Amadeusz Sadowski
 *
 */
class SendingService
{
    private final Predicate<Socket> isConnected;
    private final Logger logger = Logger.getLogger(Server.class.getName());
    private final ExecutorService sendingExecutor = Executors
            .newSingleThreadExecutor();
    private final BlockingQueue<Package> toSendQueue = new LinkedBlockingQueue<Package>();

    /**
     * Creates non-running service. To run, call {@link #startSending()}.
     * 
     * @param isConnected
     *            - used to perform check whether socket to be used for given
     *            object is connected.
     */
    public SendingService(final Predicate<Socket> isConnected)
    {
        this.isConnected = isConnected;
    }

    public void stopSending()
    {
        sendingExecutor.shutdownNow();
    }

    /**
     * Attempts adding provided arguments to queue. Fails silently.
     */
    public void send(final Object object, final ServerModuleConnection recipient)
    {
        final Package newPack = new Package(object, recipient);
        if (!toSendQueue.offer(newPack))
        {
            logger.log(Level.WARNING, "Kolejka do wysłania przepełniona");
        }
    }

    public void startSending()
    {
        sendingExecutor.execute(this::sendFromQueue);
    }

    private void sendFromQueue()
    {
        Package pack;
        while (!Thread.interrupted())
        {
            try
            {
                pack = toSendQueue.take();
                sendPack(pack);
            }
            catch (final InterruptedException e)
            {
                logger.log(Level.WARNING,
                        "Błąd oczekiwania na kolejkę wysyłania");
            }
        }
    }

    private void sendPack(final Package pack)
    {
        ObjectOutputStream oos = null;
        final Socket recipientSocket = pack.getRecipient().getSocket();
        final int recipientPort = recipientSocket.getLocalPort();
        if (!isConnected.test(recipientSocket))
        {
            logger.log(Level.WARNING, "Nie ma połączenia - nie można wysłać");
            return;
        }
        try
        {
            logger.log(Level.FINEST, "Wysyłam do " + recipientPort);
            oos = new ObjectOutputStream(recipientSocket.getOutputStream());
            oos.writeObject(pack.getObject());
            oos.flush();
            logger.log(Level.FINEST, "Wysłałem do " + recipientPort);
        }
        catch (final IOException e)
        {
            logger.log(Level.WARNING, "Błąd wysyłania", e);
        }
    }

    /**
     * Represents pair of object to be sent and destination module connection.
     * 
     * @author Amadeusz Sadowski
     */
    class Package
    {
        private final Object object;
        private final ServerModuleConnection recipient;

        public Package(final Object object,
                final ServerModuleConnection recipient)
        {
            this.object = object;
            this.recipient = recipient;
        }

        public Object getObject()
        {
            return object;
        }

        public ServerModuleConnection getRecipient()
        {
            return recipient;
        }
    }
}
