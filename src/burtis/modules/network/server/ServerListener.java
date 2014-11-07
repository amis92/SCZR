package burtis.modules.network.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import order.Order;
import order.ServerOrder;

/**
 * Klasa reprezentujaca watek odbierajacy pakiety i przekazujacy je do
 * odpowiednich modulow.
 */
class ServerListener implements Runnable
{
    private ServerModuleConnection module;
    private Server server;
    /**
     * Flaga symulowanej awarii
     */
    private boolean clogged = false;

    public ServerListener(final ServerModuleConnection module,
            final Server server)
    {
        this.module = module;
        this.server = server;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void run()
    {
        ObjectInputStream ois = null;
        while (server.isConnected(module.getSocket()))
        {
            try
            {
                ois = new ObjectInputStream(module.getSocket().getInputStream());
                System.out.println("Czekam na obiekt");
                Object object = ois.readObject();
                System.out.println("Dostalem obiekt: " + object.getClass());
                // Logger.getLogger(Server.class.getName()).log(Level.FINEST,
                // "Serwer: " + object.getClass().getName());
                // Rozsyla obiekt do wszystkich odbiorcow danego modulu.
                if (!clogged)// Jak nie ma symulowanej awarii. ~maciej168
                {
                    for (ServerModuleConnection receiver : module
                            .getReceivers())
                    {
                        if (object instanceof ServerOrder)
                        {// dodana filtracja rozkazów
                            server.executeOrder((Order) object);
                        }
                        else
                        {
                            server.send(object, receiver);
                        }
                    }
                }
            }
            catch (IOException e)
            {
                Logger.getLogger(ServerListener.class.getName()).log(
                        Level.FINER, "Ponownie łączenie", e);
                module.connect();
                break;
            }
            catch (ClassNotFoundException e)
            {
                // Nierozpoznane klasy sa ignorowane
                Logger.getLogger(Server.class.getName()).log(Level.WARNING,
                        "Ignorowanie nieznanej klasy", e);
            }
            finally
            {
                clogged = false;
            }
        }
    }

    /**
     * Symuluje awarię (nie przesyła rozkazów/wiadomości do odbiorców)
     */
    public void clog(boolean makeClogged)
    {
        clogged = makeClogged;
    }
}
