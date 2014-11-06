package network;

import order.Order;
import order.ServerOrder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Klasa reprezentujaca watek odbierajacy pakiety i przekazujacy je do odpowiednich modulow.
 */
class Listener implements Runnable {
    private ModuleNetwork module;
    private Server server;

    /**
     * Flaga symulowanej awarii ~maciej168
     */
    private boolean clogged = false;

    public Listener(final ModuleNetwork module, final Server server) {
        this.module = module;
        this.server = server;
    }

    @Override
    public void run() {
        ObjectInputStream ois = null;
        while (server.isConnected(module.getSocket())) {
            try {
                ois = new ObjectInputStream(module.getSocket().getInputStream());
                System.out.println("Czekam na obiekt");
                Object object = ois.readObject();
                System.out.println("Dostalem obiekt: " + object.getClass());

                //Logger.getLogger(Server.class.getName()).log(Level.FINEST, "Serwer: " + object.getClass().getName());

                // Rozsyla obiekt do wszystkich odbiorcow danego modulu.
                if (!clogged)// Jak nie ma symulowanej awarii. ~maciej168
                {
                    for (ModuleNetwork receiver : module.getReceivers()) {
                        if (object instanceof ServerOrder) {// dodana filtracja rozkazów ~maciej168
                            server.executeOrder((Order) object);
                        } else {
                            server.send(object, receiver);
                        }
                    }
                }
            } catch (IOException e) {
                Logger.getLogger(Listener.class.getName()).log(Level.FINER, "Ponownie łączenie", e);
                module.connect();
                break;
            } catch (ClassNotFoundException e) {
                // Nierozpoznane klasy sa ignorowane
                Logger.getLogger(Server.class.getName()).log(Level.WARNING, "Ignorowanie nieznanej klasy", e);
            } finally {
                clogged = false; // dodałem ~maciej168
            }
        }
    }

    /**
     * Symuluje awarię (nie przesyła rozkazów/wiadomości do odbiorców)
     * ~maciej168
     */
    public void clog(boolean makeClogged) {
        clogged = makeClogged;
    }
}
