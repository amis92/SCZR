package burtis.tests.modules.network;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.events.SimulationEvent;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.client.ClientConnection;
import burtis.modules.network.client.ClientModule;

/**
 *
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 * @author Amadeusz Sadowski
 */
public class TestClient
{
    /**
     * @param args
     *            the command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        logAll();
        final ModuleConfig config = NetworkConfig.defaultConfig()
                .getModuleConfigs().get(0);
        final ClientModule client = new ClientModule(config);
        System.out.println("Connecting, port " + config.getServerPort());
        client.connect();
        // sending
        System.out.println("Sending...");
        client.send(new SimulationEvent("TEST MESSAGE", new String[] { config
                .getModuleName() }));
        System.out.println("send executed");
        // receiving
        System.out.println("Taking...");
        try
        {
            System.out.println(client.getIncomingQueue().take().sender());
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        System.out.println("Naciśnij enter any zakończyć.");
        System.in.read();
        client.close();
    }

    private static void logAll()
    {
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        final Logger logger = Logger
                .getLogger(ClientConnection.class.getName());
        logger.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
    }
}
