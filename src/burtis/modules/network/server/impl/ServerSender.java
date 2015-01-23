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
    private volatile boolean isStopped = true;
    private ExecutorService sendingExecutor = Executors
            .newSingleThreadExecutor();
    private final BlockingQueue<Package> toSendQueue = new LinkedBlockingQueue<Package>();

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
            logger.warning(String.format(
                    "Sending queue overflow, lost '%1$s' addressed to '%2$s'.",
                    object.getClass().getName(), recipient.getModuleName()));
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
                logger.warning("Interrupted sending.");
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
            logger.warning(String.format(
                    "No connection - lost '%1$s' to '%2$s'.", pack.object
                            .getClass().getName(), pack.recipient
                            .getModuleName()));
            return;
        }
        logger.finest("Sending on port " + recipientPort);
        try
        {
            recipientSocketService.writeToSocket(pack.getObject());
            logger.finest("Sent on port " + recipientPort);
        }
        catch (Exception e)
        {
            logger.log(Level.WARNING, "Error sending.", e);
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
