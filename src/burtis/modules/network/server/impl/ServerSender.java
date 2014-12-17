package burtis.modules.network.server.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.modules.network.SocketService;
import burtis.modules.network.server.ModuleConnection;
import burtis.modules.network.server.Sender;
import burtis.modules.network.server.Server;

/**
 * Sends objects using blocking queue on separate thread.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class ServerSender implements Sender
{
    private final static Logger logger = Logger.getLogger(Server.class
            .getName());
    private ExecutorService sendingExecutor = Executors
            .newSingleThreadExecutor();
    private final BlockingQueue<Package> toSendQueue = new LinkedBlockingQueue<Package>();
    private volatile boolean isStopped = true;

    /**
     * Creates non-running service. To run, call {@link #startSending()}.
     */
    public ServerSender()
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
            logger.warning(String
                    .format("Kolejka do wysłania przepełniona, utracono paczkę {0} do {1}",
                            object, recipient.getModuleName()));
        }
    }

    /**
     * Starts sending in a separate thread.
     */
    public void startSending()
    {
        isStopped = false;
        sendingExecutor.execute(this::sendFromQueue);
    }

    public void stopSending()
    {
        isStopped = true;
        sendingExecutor.shutdownNow();
        sendingExecutor = Executors.newSingleThreadExecutor();
    }

    private void sendFromQueue()
    {
        Package pack;
        while (!isStopped)
        {
            try
            {
                pack = toSendQueue.take();
                sendPack(pack);
            }
            catch (final InterruptedException e)
            {
                stopSending();
                logger.warning("Przerywam wysyłanie.");
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
            logger.warning(String.format("Nie ma połączenia - utracono paczkę '{0}' do '{1}'.",
                    pack.object, pack.recipient.getModuleName()));
            return;
        }
        logger.finest("Wysyłam do " + recipientPort);
        try
        {
            recipientSocketService.writeToSocket(pack.getObject());
            logger.finest("Wysłałem do " + recipientPort);
        }
        catch (Exception e)
        {
            logger.log(Level.WARNING, "Błąd wysyłania", e);
        }
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
