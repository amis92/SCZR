package burtis.modules.sync;

import burtis.common.events.CycleCompletedEvent;
import burtis.common.events.gui.DoStepEvent;
import burtis.common.events.gui.PauseSimulationEvent;
import burtis.common.events.SimulationEvent;
import burtis.common.events.gui.StartSimulationEvent;
import burtis.common.events.TerminateSimulationEvent;
import burtis.common.events.Sync.TickEvent;
import burtis.modules.AbstractNetworkModule;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.client.ClientModule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Synchronization source for all time-dependent modules along with communication server.
 * 
 * Sends {@link TickEvent} events to all modules according to the internal state.
 * 
 * @author Mikołaj Sowiński
 *
 */
public class SimulationServer extends AbstractNetworkModule
{
    
    private static final SimulationServer sync = new SimulationServer(NetworkConfig.defaultConfig().getModuleConfigs().get(NetworkConfig.SYNC_MODULE));
    
    private Logger logger;
    
    /**
     * Simulation stopped.
     */
    private AtomicBoolean stopped = new AtomicBoolean(true);
    
    /**
     * Ready for next tick.
     */
    private AtomicBoolean readyForTick = new AtomicBoolean(false);
    
    /**
     * Number of iterations.
     */
    private AtomicLong iteration = new AtomicLong(0);
    
    /**
     * Tick thread.
     */
    private Thread tickService;
          
    private AtomicBoolean executeOnce = new AtomicBoolean(false);
    
    public static SimulationServer getInstance() {
        return sync;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public boolean executeOnce() {
        return executeOnce.get();
    }
    
    public long getIteration() {
        return iteration.get();
    }
    
    public ModuleConfig getModuleConfig() {
        return moduleConfig;
    }
    
    protected void readyForTick() {
        readyForTick.set(true);
    }
    
    protected void noReadyForTick() {
        readyForTick.set(false);
    }
    
    protected boolean isReadyForTick() {
        return readyForTick.get();
    }
    
    protected long nextIteration() {
        return iteration.getAndIncrement();
    }
         
    protected void startSimulation() 
    {
        if(stopped.get()) {
            executeOnce.set(false);
            readyForTick.set(true);
            tickService.start();
            stopped.set(false);
        }
        else {
            logger.log(Level.WARNING, "Simulation already started!");
        }
    }

    protected void pauseSimulation() {
        if(!stopped.get()) {
            doPauseSimulation();
        }
        else {
            logger.log(Level.WARNING, "Simulation is not running.");
        }
    }

    protected void doStep() {
        // Preparations
        if(tickService.isAlive()) {
            pauseSimulation();                    
        }
        logger.log(Level.INFO, "Doing one step of simulation.");
        executeOnce.set(true);
        tickService.start();
    }

    @Override
    protected void init() {
        System.out.println("Modules:\n========\n");
        
        for(ModuleConfig module : NetworkConfig.defaultConfig().getModuleConfigs()) {
            if(module.getModuleName() != moduleConfig.getModuleName()) {
                Module.add(module.getModuleName(), module.isCritical());
                System.out.println(" * " + module.getModuleName() + (module.isCritical() ? "(critical)\n" : "\n"));
            }
        }
 
        tickService = new Thread(new TickTask());
        Module.resetModuleWatchdog();
    }

    @Override
    protected void terminate() {
        logger.log(Level.INFO, "Sending termination signal to modules...");
        send(new TerminateSimulationEvent(moduleConfig.getModuleName()));
        
        logger.log(Level.INFO, "Shutting down processes...");
        tickService.interrupt();
        try {
            tickService.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(SimulationServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        Module.stopModuleWatchdog();
    }

    public SimulationServer(ModuleConfig config) 
    {   
        super(config);
        
        logger = Logger.getLogger(SimulationServer.class.getName()); 
    }
    

    
    
    
    /**
     * Tries to pause simulation and waits for result.
     * If service is not in terminated state in 5 seconds an exception is thrown.
     */
    private void doPauseSimulation()
    {
        long t0 = System.nanoTime();
        
        logger.log(Level.INFO, "Pausing simulation...");
        tickService.interrupt();
        Module.interruptModuleWatchdog();
//        try {
//            while(tickService.isAlive() || watchdogService.isAlive()) {
//                if(System.nanoTime() > t0 + 5*10e9) {
//                    throw(new Exception("Can not pause simulation!"));
//                }
//            }
//            logger.log(Level.INFO, "Simulation paused.");
//        }
//        catch(Exception e) {
//            handlePauseTimeoutException(e);
//        }
    }

    /**
     * Handles pause operation exceptions.
     * If pause fails to be done an application is to be terminated.
     * @param e Exception
     */
    private void handlePauseTimeoutException(Exception e) 
    {
        logger.log(Level.SEVERE, e.getMessage());
        logger.log(Level.SEVERE, "Terminating simulation server.");
        terminate(); 
    }

    /**
     * Main method for application.
     * @param args No parameters are expected.
     */
    public static void main(String[] args)
    {
        SimulationServer app = SimulationServer.getInstance();
        app.eventHandler = new SyncEventHandler();
        app.main();
        
    }
}
