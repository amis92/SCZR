package burtis.tests.modules.sync;

import burtis.common.events.CycleCompletedEvent;
import burtis.common.events.EventProcessor;
import burtis.common.events.SimulationEvent;
import burtis.common.events.gui.StartSimulationEvent;
import burtis.common.events.TerminateSimulationEvent;
import burtis.common.events.Sync.TickEvent;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.client.ClientModule;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimulationModuleTest extends EventProcessor implements Runnable
{
    private ModuleConfig config;
    private ModuleConfig simServer;
    private ClientModule client;
    private Thread mainThread;
    
    private long iteration;

    public SimulationModuleTest() {
        config = NetworkConfig.defaultConfig().getModuleConfigs().get(0);
        simServer = NetworkConfig.defaultConfig().getModuleConfigs().get(1);
        client = new ClientModule(config);
        mainThread = new Thread(this);
        
        try {
            client.connect();
        } catch (IOException ex) {
            Logger.getLogger(SimulationModuleTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            System.exit(1);
        }
    }
        
    private void tickEvent(TickEvent event) {
        iteration = event.iteration();
        System.out.println("Iteration " + iteration);
        client.send(new CycleCompletedEvent(config.getModuleName(), iteration));
    }
    
    public void terminate() {
        mainThread.interrupt();
        client.close();       
    }
    
    public void start() {
        client.send(new StartSimulationEvent(config.getModuleName(), 
                new String[] { simServer.getModuleName() }));
        mainThread.start();
    }
    
    @Override
    public void run() {
        SimulationEvent event;
        
        while(!Thread.interrupted()) {
            try {
                System.out.println("Waiting for tick...");
                event = client.getIncomingQueue().take();
                if(event instanceof TickEvent) {
                    tickEvent((TickEvent)event);
                }
                else {
                    System.out.println("Unknown event " + event.getClass().getSimpleName());
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(SimulationModuleTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                terminate();
            }
        }
    }
        
    public static void main(String[] args)
    {
       SimulationModuleTest app = new SimulationModuleTest();
       app.start();
        try {
            System.out.println("ENTER");
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(SimulationModuleTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        app.terminate();
    }

    
}
