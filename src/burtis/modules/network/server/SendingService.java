package burtis.modules.network.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.modules.network.client.Client;

class SleepingSender
{
    private final Logger logger = Logger.getLogger(Client.class.getName());
    private Thread sendingThread;
    private Server server;
    private BlockingQueue<Package> toSend;
    Runnable sending = new Runnable() {
        @Override
        public void run()
        {
            Package pack;
            while (!Thread.interrupted())
            {
                try
                {
                    pack = toSend.take();
                    Socket receiverSocket = pack.getReceiver().getSocket();
                    if (server.isConnected(receiverSocket))
                    {
                        ObjectOutputStream oos = null;
                        try
                        {
                            logger.log(Level.FINEST,
                                    "Wysylam" + receiverSocket.getLocalPort());
                            oos = new ObjectOutputStream(
                                    receiverSocket.getOutputStream());
                            oos.writeObject(pack.getObject());
                            oos.flush();
                            logger.log(Level.FINEST, "Wyslalem"
                                    + receiverSocket.getLocalPort());
                        }
                        catch (SocketException e)
                        {
                            logger.log(Level.WARNING, "Błąd wysyłania");
                        }
                        catch (IOException e)
                        {
                            logger.log(Level.WARNING, "Błąd wysyłania");
                        }
                    }
                }
                catch (InterruptedException e)
                {
                    logger.log(Level.WARNING,
                            "Błąd przy oczekiwaniu na rozkaz do wyslania");
                }
            }
        }
    };

    SleepingSender(final Server server)
    {
        toSend = new LinkedBlockingQueue<Package>();
        this.server = server;
    }

    public void send(final Object object, final ServerModuleConnection receiver)
    {
        if (!toSend.offer(new Package(object, receiver)))
        {
            logger.log(Level.WARNING, "Rozkazy sa wysylane za szybko!");
        }
    }

    public void startSending()
    {
        sendingThread = new Thread(sending);
        sendingThread.start();
    }

    class Package
    {
        private final Object object;
        private final ServerModuleConnection receiver;

        Package(final Object object, final ServerModuleConnection receiver)
        {
            this.object = object;
            this.receiver = receiver;
        }

        Object getObject()
        {
            return object;
        }

        ServerModuleConnection getReceiver()
        {
            return receiver;
        }
    }
}
