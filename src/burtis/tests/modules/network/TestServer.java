package burtis.tests.modules.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.server.Server;

public class TestServer
{
    public static void main(String[] args) throws InterruptedException
    {
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        final Logger logger = Logger.getLogger(Server.class.getName());
        logger.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
        List<ModuleConfig> configs = new ArrayList<ModuleConfig>(2);
        configs.add(new ModuleConfig("clk", 8123, new String[] { "proc1" }));
        configs.add(new ModuleConfig("proc1", 8124, new String[] { "clk" }));
        Server server = new Server(new NetworkConfig("127.0.0.1", configs));
        server.run();
        try
        {
            System.in.read();
        }
        catch (IOException ex)
        {
            Logger.getLogger(TestServer.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        server.stop();
    }
}
