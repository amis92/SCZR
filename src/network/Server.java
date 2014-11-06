package network;

import order.FunctionalityServer;
import order.Order;
import order.OrderRecipient;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa serwera.
 * Zajmuje sie rozsylaniem otrzymanych obiektow do odpowiednich modulow.
 *
 * @author Maciej Korpalski
 */
// Dodałem symulację awarii ~maciej168
// Dorzuciłem Loggery. Czemu? Bo można je zajebiście filtrować. W kodzie używającym tej klasy wystarczy że walniesz
// Logger.getLogger(Server.class .getName()).setLevel(Level.OFF) i już nie widać żadnych komunikatów. Cudny mechanizm, polecam
// ~maciej168
// Dodałem obsługę rozkazów

public class Server implements FunctionalityServer, OrderRecipient<FunctionalityServer> {
    /**
     * Obiekt reprezentujacy modul GUI
     */
    private ModuleNetwork gui;
    /**
     * Obiekt reprezentujacy modul obslugi pasazerow
     */
    private ModuleNetwork passengers;
    /**
     * Obiekt reprezentujacy modul zarzadzania komunikacja miejska
     */
    private ModuleNetwork management;

    private SleepingSender sleepingSender;
    private ExecutorService executor;

    private int guiPort;
    private int passengersPort;
    private int managementPort;


    /**
     * Tworzy moduly i przypisuje im odpowiednie porty sieciowe.
     */
    public Server(int guiPort, int passengersPort, int managementPort) {
        this.guiPort = guiPort;
        this.passengersPort = passengersPort;
        this.managementPort = managementPort;
        executor = Executors.newCachedThreadPool();
    }

    /**
     * Otwiera polaczenia dla wszystkich modulow,
     * przypisuje kazdemu modulowi odpowiadajacych mu odbiorcow.
     */
    public void runServer() throws IOException {
        try {
            gui = new ModuleNetwork(guiPort, this);
            passengers = new ModuleNetwork(passengersPort, this);
            management = new ModuleNetwork(managementPort, this);
            sleepingSender = new SleepingSender(this);
        } catch (IOException e) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Błąd tworzenia Serwera"); // dodałem ~maciej168
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

    @Override // dodane ~maciej168
    public void crippleGUI(boolean cripple) {
        Logger.getLogger(Server.class.getName()).log(Level.FINEST, "Odłączenie GUI");
        gui.clog(cripple);
    }

    @Override // dodane ~maciej168
    public void crippleZKM(boolean cripple) {
        Logger.getLogger(Server.class.getName()).log(Level.FINEST, "Odłączenie ZKM");
        management.clog(cripple);
    }

    @Override // dodane ~maciej168
    public void executeOrder(Order<FunctionalityServer> toExec) {
        Logger.getLogger(Server.class.getName()).log(Level.FINEST, "Wykonywanie rozkazu");
        toExec.execute(this);
    }

    /**
     * Wysyla obiekt do konkretnego socketa
     */
    void send(final Object object, ModuleNetwork receiver) {
        sleepingSender.send(object, receiver);
    }

    /**
     * Informuje czy z socketem jest polaczony jakis klient.
     */
    boolean isConnected(final Socket socket) {
        if (socket == null) {
            return false;
        } else {
            return !socket.isClosed();
        }
    }

    /**
     * Rozlacza klienta z konkretnego socketa.
     */
    void closeConnection(Socket socket) {
        if (isConnected(socket)) {
            try {
                socket.close();
            } catch (IOException e) {
                //e.printStackTrace(); //zmieniłem ~maciej168
                Logger.getLogger(Server.class.getName()).log(Level.WARNING, "Błąd przy zamykaniu połączenia", e);
                throw new RuntimeException();
            }
        }
    }

    // server to moduł, więc niech mozże umie się zastartować bez viagry
    public static void main(String[] args) {
        try {
            Server server = new Server(8123, 8124, 8125);
            server.runServer();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
