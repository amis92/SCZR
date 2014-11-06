package network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa, ktora posiada wlasny watek do wysylania obiektow w swiat.
 * Przydatne, zeby uniknac zakleszczen, tzn zeby nie wysylac w tym samym watku,
 * w ktorym odbieramy lub zeby nie tworzyc ogromnej ilosci watkow wysylajacych.
 */
class SleepingSender {
    /**
     * Watek, ktory oczekuje na pojawienie sie rozkazu w kolejce,
     * a nastepnie wysyla go do odpowiedniego odbiorcy.
     */
    Runnable sending = new Runnable() {
        @Override
        public void run() {
            Package pack;
            while (!Thread.interrupted()) {
                try {
                    pack = toSend.take();
                    Socket receiverSocket = pack.getReceiver().getSocket();
                    if (server.isConnected(receiverSocket)) {
                        ObjectOutputStream oos = null;
                        try {
                            System.out.println("Wysylam" + receiverSocket.getLocalPort());
                            oos = new ObjectOutputStream(receiverSocket.getOutputStream());
                            oos.writeObject(pack.getObject());
                            oos.flush();
                            System.out.println("Wyslalem" + receiverSocket.getLocalPort());

                        } catch (SocketException e) {
                            Logger.getLogger(Server.class.getName()).log(Level.WARNING, "Błąd wysyłania"); // dodałem ~maciej168
                            e.printStackTrace();
                            pack.getReceiver().reportConnectionProblem();
                        } catch (IOException e) {
                            Logger.getLogger(Server.class.getName()).log(Level.WARNING, "Błąd wysyłania"); // dodałem ~maciej168
                            e.printStackTrace();
                        }
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    Logger.getLogger(Server.class.getName()).log(Level.WARNING, "Błąd przy oczekiwaniu na rozkaz do wyslania");
                    e.printStackTrace();
                }

            }
        }
    };
    private Thread sendingThread;
    /**
     * Kolejka przechowujaca rozkazy do wyslania
     */
    private BlockingQueue<Package> toSend;
    private Server server;

    SleepingSender(final Server server) {
        toSend = new LinkedBlockingQueue<Package>();
        this.server = server;
    }

    /**
     * Rozpoczyna watek rozsylajacy rozkazy
     */
    public void startSending() {
        sendingThread = new Thread(sending);
        sendingThread.start();
    }

    /**
     * Metoda, ktora dodaje obiekt i odbiorce do kolejki
     */
    public void send(final Object object, final ModuleNetwork receiver) {
        if (!toSend.offer(new Package(object, receiver))) {
            Logger.getLogger(Server.class.getName()).log(Level.WARNING, "Rozkazy sa wysylane za szybko!");
        }
    }

    /**
     * Klasa reprezentujaca pojedynczy pakiet: obiekt - odbiorca
     */
    class Package {
        private final Object object;
        private final ModuleNetwork receiver;

        Package(final Object object, final ModuleNetwork receiver) {
            this.object = object;
            this.receiver = receiver;
        }

        Object getObject() {
            return object;
        }

        ModuleNetwork getReceiver() {
            return receiver;
        }
    }
}
