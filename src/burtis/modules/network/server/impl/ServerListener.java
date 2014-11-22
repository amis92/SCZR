package burtis.modules.network.server.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.modules.network.server.Action;
import burtis.modules.network.server.Server;
import burtis.modules.network.server.SocketService;

/**
 * Listens for incoming traffic and forwards it to provided Consumer.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class ServerListener
{
    private final Logger logger = Logger.getLogger(Server.class.getName());
    private final Consumer<Object> receiver;
    private final Action reconnect;
    private final SocketService socketService;

    public ServerListener(final SocketService socketService,
            final Consumer<Object> receiver, final Action reconnect)
    {
        this.socketService = socketService;
        this.receiver = receiver;
        this.reconnect = reconnect;
    }

    public void listenOnSocket()
    {
        final int port = socketService.getPort();
        logger.log(Level.INFO, "Rozpoczęcie nasłuchiwania na porcie: " + port);
        while (!Thread.interrupted() && socketService.isConnected())
        {
            socketService.readFromSocket(this::awaitAndAccept);
        }
        logger.log(Level.INFO, "Koniec nasłuchiwania na porcie: " + port);
    }

    private void awaitAndAccept(Socket socket)
    {
        try
        {
            ObjectInputStream ois = new ObjectInputStream(
                    socket.getInputStream());
            logger.log(Level.FINEST, "Czekam na obiekt");
            Object object = ois.readObject();
            logger.log(Level.FINEST, "Dostalem obiekt: "
                    + object.getClass().getName());
            receiver.accept(object);
        }
        catch (ClassNotFoundException e)
        {
            logger.log(Level.WARNING, "Ignorowanie nieznanej klasy", e);
        }
        catch (IOException e)
        {
            logger.log(Level.WARNING, "Błąd. Ponownie łączenie", e);
            socketService.close();
            reconnect.perform();
        }
    }
}
