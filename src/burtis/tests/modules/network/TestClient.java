/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates and open the template
 * in the editor.
 */
package burtis.tests.modules.network;

import burtis.common.events.SimulationEvent;
import burtis.modules.network.client.ClientConnection;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
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
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        final Logger logger = Logger.getLogger(ClientConnection.class.getName());
        logger.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
        ClientConnection<SimulationEvent> client = new ClientConnection<>("127.0.0.1",
                Integer.parseInt(args[0]));
        System.out.print("Connecting, port " + args[0]);
        client.connect();
        while (!client.isConnected())
        {
            System.out.print(".");
        }
        if (args[1].equals("s"))
        {
            System.out.println("\nSending...");
            client.send(new SimulationEvent("TEST MESSAGE"));
            System.out.println("send executed");
        }
        else
        {
            System.out.println("\nTaking...");
            try
            {
                System.out.println(client.getIncomingQueue().take()
                        .sender());
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
        System.in.read();
        client.close();
    }
}
