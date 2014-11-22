package burtis.modules.network.server;

import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sends objects using blocking queue on separate thread.
 * 
 * @author Amadeusz Sadowski
 *
 */
class SendingService implements Sender
{
    private final Logger logger = Logger.getLogger(Server.class.getName());
    private ExecutorService sendingExecutor = Executors
            .newSingleThreadExecutor();
    private final BlockingQueue<Package> toSendQueue = new LinkedBlockingQueue<Package>();

    /**
     * Creates non-running service. To run, call {@link #startSending()}.
     */
    public SendingService()
    {
    }

    /**
     * Attempts adding provided arguments to queue. Fails silently.
     */
    @Override
    public void send(final Object object, final ModuleConnection recipient)
    {
        final Package newPack = new Package(object, recipient);
        if (!toSendQueue.offer(newPack))
        {
            logger.log(Level.WARNING, "Kolejka do wysłania przepełniona");
        }
    }

    /**
     * Starts sending in a separate thread.
     */
    public void startSending()
    {
        sendingExecutor.execute(this::sendFromQueue);
    }

    public void stopSending()
    {
        sendingExecutor.shutdownNow();
        sendingExecutor = Executors.newSingleThreadExecutor();
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
        final SocketService recipientSocketService = pack.getRecipient()
                .getSocketService();
        final int recipientPort = recipientSocketService.getPort();
        
        if (!recipientSocketService.isConnected())
        {
            logger.log(Level.WARNING, "Nie ma połączenia - utracono paczkę");
            return;
        }
        logger.log(Level.FINEST, "Wysyłam do " + recipientPort);
        recipientSocketService.useSocket((socket) ->
        {
            ObjectOutputStream oos = null;
            try
            {
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(pack.getObject());
                oos.flush();
                logger.log(Level.FINEST, "Wysłałem do " + recipientPort);
            }
            catch (Exception e)
            {
                logger.log(Level.WARNING, "Błąd wysyłania", e);
            }
        });
    }

    /**
     * Represents pair of object to be sent and destination module connection.
     * 
     * @author Amadeusz Sadowski
     */
    private class Package
    {
        private final Object object;
        private final ModuleConnection recipient;

        public Package(final Object object, final ModuleConnection recipient)
        {
            this.object = object;
            this.recipient = recipient;
        }

        public Object getObject()
        {
            return object;
        }

        public ModuleConnection getRecipient()
        {
            return recipient;
        }
    }
}
