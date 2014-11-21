package burtis.modules.network;

import java.util.ArrayList;
import java.util.List;

import burtis.common.events.SimulationEvent;
import burtis.modules.network.client.Client;
import burtis.modules.network.server.Server;

public class ServerApp
{

    public static void main(String[] args) throws InterruptedException
    {
                        
        List<ModuleConfig> configs = new ArrayList<ModuleConfig>(2);
        configs.add(new ModuleConfig("clk", 8123, new String[]{"proc1"}));
        configs.add(new ModuleConfig("proc1", 8124, new String[]{"clk"}));
        
        Server server = new Server(new NetworkConfig("127.0.0.1", configs));
        server.run();
        
        Client<SimulationEvent> clk = new Client<SimulationEvent>("127.0.0.1", 8123);
        clk.connect();
                
        Client<SimulationEvent> proc1 = new Client<SimulationEvent>("127.0.0.1", 8124);
        proc1.connect();
        
        System.out.println("Waiting for connection...");
        while(!proc1.isConnected() || !clk.isConnected()) {}
        
        System.out.println("Sending...");
        clk.send(new SimulationEvent("EEE"));
        
        System.out.println("Taking... " + clk.isConnected() + " " + proc1.isConnected());
        System.out.println(proc1.getEventsBlockingQueue().take().sender());
                  
        clk.closeConnection();
        proc1.closeConnection();
        server.stop();
        
    }
}
