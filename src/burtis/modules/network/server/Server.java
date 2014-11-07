package burtis.modules.network.server;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import order.FunctionalityServer;
import order.Order;
import order.OrderRecipient;

public class Server implements FunctionalityServer,
        OrderRecipient<FunctionalityServer>
{
    /**
     * Obiekt reprezentujacy modul GUI
     */
    private ServerModuleConnection gui;
    /**
     * Obiekt reprezentujacy modul obslugi pasazerow
     */
    private ServerModuleConnection passengers;
    /**
     * Obiekt reprezentujacy modul zarzadzania komunikacja miejska
     */
    private ServerModuleConnection management;
    private SleepingSender sleepingSender;
    private int guiPort;
    private int passengersPort;
    private int managementPort;


    public Server(int guiPort, int passengersPort, int managementPort)
    {
        this.guiPort = guiPort;
        this.passengersPort = passengersPort;
        this.managementPort = managementPort;
    }

    public void runServer() throws IOException
    {
        try
        {
            gui = new ServerModuleConnection(guiPort, this);
            passengers = new ServerModuleConnection(passengersPort, this);
            management = new ServerModuleConnection(managementPort, this);
            sleepingSender = new SleepingSender(this);
        }
        catch (IOException e)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE,
                    "Błąd tworzenia Serwera");
            throw e;
        }
        sleepingSender.startSending();
        gui.connect();
        passengers.connect();
        management.connect();
        gui.addReceiver(passengers);
        passengers.addReceiver(gui);
        passengers.addReceiver(management);
        management.addReceiver(passengers);
    }

    @Override
    public void crippleGUI(boolean cripple)
    {
        Logger.getLogger(Server.class.getName()).log(Level.FINEST,
                "Odłączenie GUI");
        gui.clog(cripple);
    }

    @Override
    public void crippleZKM(boolean cripple)
    {
        Logger.getLogger(Server.class.getName()).log(Level.FINEST,
                "Odłączenie ZKM");
        management.clog(cripple);
    }

    @Override
    public void executeOrder(Order<FunctionalityServer> toExec)
    {
        Logger.getLogger(Server.class.getName()).log(Level.FINEST,
                "Wykonywanie rozkazu");
        toExec.execute(this);
    }

    void send(final Object object, ServerModuleConnection receiver)
    {
        sleepingSender.send(object, receiver);
    }

    boolean isConnected(final Socket socket)
    {
        if (socket == null)
        {
            return false;
        }
        else
        {
            return !socket.isClosed();
        }
    }

    void closeConnection(Socket socket)
    {
        if (isConnected(socket))
        {
            try
            {
                socket.close();
            }
            catch (IOException e)
            {
                Logger.getLogger(Server.class.getName()).log(Level.WARNING,
                        "Błąd przy zamykaniu połączenia", e);
                throw new RuntimeException();
            }
        }
    }

    public static void main(String[] args) throws IOException
    {
        Server server = new Server(8123, 8124, 8125);
        server.runServer();
    }
}
