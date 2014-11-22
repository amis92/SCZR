package burtis.tests.modules.network;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.modules.network.NetworkConfig;
import burtis.modules.network.server.Server;

public class TestServer
{
    public static void main(String[] args) throws InterruptedException, IOException
    {
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        final Logger logger = Logger.getLogger(Server.class.getName());
        logger.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
        Server server = new Server(NetworkConfig.defaultConfig());
        server.run();
        System.out.println("Naciśnij enter any zakończyć.");
        System.in.read();
        server.stop();
    }
}
