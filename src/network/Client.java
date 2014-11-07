package network;

import main.SimulatorConstants;
import view.SimulatorEvent;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klient przesylajacy i odbierajacy obiekty.
 *
 * @author Maciej Korpalski
 */
public class Client<ChujPodluzny> {
    /**
     * Socket dla serwera
     */
    private Socket socket;
    /**
     * Kolejka, do ktorej sa wrzucane otrzymane rozkazy
     */
    private LinkedBlockingQueue<ChujPodluzny> eventsBlockingQueue;
    private String serverAddress;
    private int serverPort;
    /**
     * Uzywane w metodzie reportConnectionProblem,
     * zeby zapobiec wielokrotnemu uruchamiania watku laczenia.
     */
    private volatile AtomicBoolean connectingProgress;
    ExecutorService executorService;

    /**
     * Implementacja watku odbierajacego obiekty od serwera
     */
    private Runnable receive = new Runnable() {
        @Override
        public void run() {
            ObjectInputStream ois = null;
            while (!socket.isClosed()) {
                try {
                    System.out.println("Czekam " + socket.getLocalPort());
                    ois = new ObjectInputStream(socket.getInputStream());
                    Object object = ois.readObject();
                    System.out.println("Dostalem: " + object.getClass());
                    eventsBlockingQueue.add((ChujPodluzny) object);
                } catch (IOException e) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "Błąd odbierania z serwera", e); // dodałem ~maciej168
                    reportConnectionProblem();
                } catch (ClassNotFoundException e) {
                    // Nierozpoznane klasy sa ignorowane
                    //e.printStackTrace(); //zmieniłem ~maciej168
                    Logger.getLogger(Client.class.getName()).log(Level.WARNING, "Ignorowanie nieznanej klasy", e);
                }
            }
        }
    };

    public Client(final String serverAddress, final int serverPort) {
        this.eventsBlockingQueue = new LinkedBlockingQueue<ChujPodluzny>();
        socket = new Socket();
        connectingProgress = new AtomicBoolean(false);
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        executorService = Executors.newSingleThreadExecutor();
    }

    public void setEventsBlockingQueue(LinkedBlockingQueue<ChujPodluzny> eventsBlockingQueue) {
        this.eventsBlockingQueue = eventsBlockingQueue;
    }

    /**
     * Laczy z serwerem i uruchamia watek odbierajacy obiekty.
     *
     * @return boolean - polaczenie udane/nieudane
     */
    public boolean connect() {
        connectingProgress.set(true);
        closeConnection();
        try {
            socket.connect(new InetSocketAddress(serverAddress, serverPort), SimulatorConstants.connectingTimeout);
            executorService.execute(receive);
            return true;
        } catch (Exception e) {
            Logger.getLogger(Client.class.getName()).log(Level.FINE, "Błąd łączenia z serwerem"); // dodałem ~maciej168
            closeConnection();
        }
        finally {
            connectingProgress.set(false);
        }
        return false;
    }

    /**
     * Wysyla obiekt do serwera
     *
     * @return true jezeli udalo sie wyslac, false jezeli wystapil blad
     */
    public boolean send(final Object object) {
        if (socket.isBound()) {
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(object);
                oos.flush();
                return true;
            } catch (IOException e) {
                Logger.getLogger(Client.class.getName()).log(Level.WARNING, "Błąd wysyłania do serwera", e); // dodałem ~maciej168
                e.printStackTrace();
                closeConnection();
                return false;
            }
        }
        return false;
    }

    /**
     * Metoda przyjmujaca zgloszenia o problemie z polaczeniem
     */
    public void reportConnectionProblem() {
        boolean connected = false;
        while (!connectingProgress.get() && !connected) {
            connected = connect();
        }
    }

    /**
     * Zamkniecie polaczenia z serwerem
     */
    public void closeConnection() {
        if (isConnected()) {
            try {
                socket.close();
                socket = new Socket();
            } catch (IOException e) {
                executorService.shutdownNow();
                socket = new Socket();
            }
        } else {
            socket = new Socket();
        }
    }

    /**
     * Informuje czy z socketem jest polaczony jakis serwer.
     */
    private boolean isConnected() {
        if (socket == null) {
            return false;
        } else {
            return socket.isBound();
        }
    }

    public LinkedBlockingQueue<ChujPodluzny> getEventsBlockingQueue() {
        return eventsBlockingQueue;
    }
}
