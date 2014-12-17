package burtis.modules.network;

import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.modules.network.server.Action;

/**
 * Listens for incoming traffic and forwards it to provided Consumer.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class ListenerImpl implements Listener
{
    private final Logger logger;
    private final Consumer<Object> receiver;
    private final Action reconnect;
    private final SocketService socketService;
    private volatile boolean isInterrupted = false;

    public ListenerImpl(final SocketService socketService,
            final Consumer<Object> receiver, final Action reconnect,
            final Logger logger)
    {
        this.socketService = socketService;
        this.receiver = receiver;
        this.reconnect = reconnect;
        this.logger = logger;
    }

    @Override
    public void listen()
    {
        final int port = socketService.getPort();
        logger.log(Level.INFO, "Rozpoczęcie nasłuchiwania na porcie " + port);
        while (!isInterrupted && socketService.isConnected())
        {
            awaitAndAccept();
        }
        logger.log(Level.INFO, "Koniec nasłuchiwania na porcie " + port);
    }

    private void awaitAndAccept()
    {
        try
        {
            socketService.readFromSocket(receiver);
        }
        catch (final AsynchronousCloseException e)
        {
            isInterrupted = true;
            logger.info("Interrupt - Przerywam nasłuchiwanie.");
        }
        catch (IOException e)
        {
            logger.log(Level.WARNING, "Błąd. Ponownie łączenie.");
            socketService.close();
            reconnect.perform();
        }
    }
}
