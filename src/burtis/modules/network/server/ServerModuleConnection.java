package burtis.modules.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages connection on given port. Awaits for incoming connection. After it's
 * setup, a new {@link ServerListener} is created and run for it.
 * 
 * Provides list of receivers to whom {@link ServerListener} will forward
 * incoming communication.
 * 
 * @author Amadeusz Sadowski
 *
 */
class ServerModuleConnection
{
    /**
     * Socket klienta
     */
    private Socket socket;
    /**
     * Socket serwera
     */
    private ServerSocket serverSocket;
    /**
     * Lista odbiorcow, ktorym nalezy przekazywac pakiety
     */
    private List<ServerModuleConnection> receivers;
    /**
     * Handle klasy roboczej. Potrzebny do symulowania awarii ~maciej168
     */
    private ServerListener currentListener;
    private Server server;
    private ExecutorService executor;
    /**
     * Uzywane w metodzie reportConnectionProblem, zeby zapobiec wielokrotnemu
     * uruchamiania watku laczenia.
     */
    private volatile AtomicBoolean connectingProgress;

    // TODO ctors shouldn't throw exceptions. factory should be created for this
    // purpose.
    ServerModuleConnection(final int port, final Server server)
            throws IOException
    {
        connectingProgress = new AtomicBoolean(false);
        this.server = server;
        receivers = new ArrayList<ServerModuleConnection>();
        try
        {
            this.serverSocket = new ServerSocket(port);
        }
        catch (IOException e)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE,
                    "Błąd tworzenia ServerSocket'u. Pewnie port jest zajęty.");
            throw e;
        }
        executor = Executors.newCachedThreadPool();
    }

    public Socket getSocket()
    {
        return socket;
    }

    public List<ServerModuleConnection> getReceivers()
    {
        return receivers;
    }

    /**
     * Otwiera polaczenie i oczekuje na klienta. Po ustanowieniu polaczenia
     * uruchamia dla tego modulu watek odbierajacy pakiety.
     */
    public void connect()
    {
        Runnable connecting = new Runnable() {
            @Override
            public void run()
            {
                connectingProgress.set(true);
                server.closeConnection(socket);
                try
                {
                    socket = serverSocket.accept();
                    currentListener = new ServerListener(
                            ServerModuleConnection.this, server);
                    executor.execute(currentListener);
                    System.out.println("Podlaczyl sie klient - port: "
                            + serverSocket.getLocalPort());
                }
                catch (IOException e)
                {
                    Logger.getLogger(Server.class.getName()).log(Level.WARNING,
                            "Błąd tworzenia Listener'a", e);
                    throw new RuntimeException();
                }
                finally
                {
                    connectingProgress.set(false);
                }
            }
        };
        executor.execute(connecting);
    }

    /**
     * Dodaje nowego odbiorce dla tego modulu.
     */
    public void addReceiver(final ServerModuleConnection module)
    {
        receivers.add(module);
    }

    /**
     * Metoda przyjmujaca zgloszenia o problemie z polaczeniem
     */
    public void reportConnectionProblem()
    {
        if (!connectingProgress.get())
        {
            connect();
        }
    }

    /**
     * Symulacja awarii modułu sieciowego. ~maciej168
     */
    public void clog(boolean makeClogged)
    {
        currentListener.clog(makeClogged);
    }
}
