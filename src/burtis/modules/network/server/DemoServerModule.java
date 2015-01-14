package burtis.modules.network.server;

import java.io.IOException;
import java.util.logging.Level;

import burtis.modules.network.NetworkConfig;

/**
 * Demo program running Server with default configuration until any input is
 * detected.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class DemoServerModule extends Server
{
    public DemoServerModule()
    {
        super(NetworkConfig.defaultConfig());
    }

    public static void main(String[] args)
    {
        DemoServerModule server = new DemoServerModule();
        try
        {
            server.run();
            System.out.println(">>> Press enter to close application.");
            System.in.read();
        }
        catch (IOException ex)
        {
            Server.logger.log(Level.SEVERE, null, ex);
        }
        if (server.isRunning)
        {
            server.stop();
        }
    }
}
