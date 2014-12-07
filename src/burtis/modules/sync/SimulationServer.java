package burtis.modules.sync;

import burtis.common.events.CycleCompletedEvent;
import burtis.common.events.gui.DoStepEvent;
import burtis.common.events.gui.PauseSimulationEvent;
import burtis.common.events.SimulationEvent;
import burtis.common.events.gui.StartSimulationEvent;
import burtis.common.events.TerminateSimulationEvent;
import burtis.common.events.Sync.TickEvent;
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
public class SimulationServer implements Runnable
{
    
    private static final Logger logger = Logger.getLogger(SimulationServer.class.getName());
    
    private final ClientModule client;
      
    /**
     * List of module states.
     */
    private final Map<String,Boolean> moduleStates = new HashMap<>();
    
    /**
     * List of module criticalness.
     */
    private final Map<String,Boolean> moduleCriticalness = new HashMap<>();
    
    /**
     * List of module criticalness.
     */
    private final Map<String,Boolean> moduleIgnored = new HashMap<>();
    
    /**
     * List of modules taking part in simulation.
     */
    private final List<String> modules = new ArrayList<>();
    
    /**
     * Sync module configuration.
     */
    private final ModuleConfig config;
    
    /**
     * Main execution loop thread.
     */
    private final Thread mainThread;
    
    /**
     * Desired time of single iteration, given in ms.
     */
    private int iterationTime;
    
    /**
     * Timeout for response from modules, given in ms.
     */
    private final int moduleResponseTimeout;
    
    /**
     * Poll timeout.
     */
    private final long pollTimeout = 1000;
             
    /**
     * Stops simulation at every iteration.
     */
    private AtomicBoolean stepMode = new AtomicBoolean();
    
    /**
     * Simulation stopped.
     */
    private AtomicBoolean stopped = new AtomicBoolean();
    
    /**
     * Ready for next tick.
     */
    private AtomicBoolean readyForTick = new AtomicBoolean();
    
    /**
     * Number of iterations.
     */
    private AtomicLong iteration = new AtomicLong();
    
    /**
     * Tick thread.
     */
    private Thread tickService;
    
    /**
     * Dead module watchdog.
     */
    private Thread watchdogService;
        
    private AtomicBoolean executeOnce = new AtomicBoolean(false);
    
    private void handleEvent(SimulationEvent event) {
        
        if(event instanceof StartSimulationEvent) {
            startSimulation();            
        }
        else if(event instanceof PauseSimulationEvent) {
            pauseSimulation();
        }
        else if(event instanceof DoStepEvent) {
            doStep();
        }
        else if(event instanceof CycleCompletedEvent) {
            cycleCompleted((CycleCompletedEvent)event);
        }
        else {
            defaultEventHandler(event);
        }
    }
        
    private void startSimulation() 
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

    private void pauseSimulation() {
        if(!stopped.get()) {
            doPauseSimulation();
        }
        else {
            logger.log(Level.WARNING, "Simulation is not running.");
        }
    }

    private void doStep() {
        // Preparations
        if(tickService.isAlive()) {
            pauseSimulation();                    
        }
        logger.log(Level.INFO, "Doing one step of simulation.");
        executeOnce.set(true);
        tickService.start();
    }

    private void cycleCompleted(CycleCompletedEvent event) {
        // Being not in sync is uncacceptable.
        if(event.iteration() != iteration.get()) {
            logger.log(Level.SEVERE, "Synchronization error. "
                    + "Module {0} is not in sync.", event.sender());
            terminateServer();
        }
        else {
            moduleStates.put(event.sender(), true);
            checkIgnoredModules(event);
            checkNextIterationClearance();
        }
    }
    
    private void defaultEventHandler(SimulationEvent event) {
        logger.log(Level.WARNING, "Unhandeled event type: {0}", event.getClass().getSimpleName());
    }
    
    /**
     * Class implementing thread responsible for sending ticks.
     */
    private class TickTask implements Runnable
    {
                
        @Override
        public void run() {
            
            while(!Thread.interrupted()) {
                            
                while(!readyForTick.get()) {
                    if(Thread.interrupted()) return;
                }

                if(!Thread.interrupted()) {
                    
                    readyForTick.set(false);
  
                    for(String moduleName : modules) {
                        moduleStates.put(moduleName, false);
                    }

                    iteration.getAndIncrement();
                    
                    if(watchdogService.isAlive()) {
                        watchdogService.interrupt();
                        try {
                            watchdogService.join();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(SimulationServer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                            terminateServer();
                        }
                    }
                    watchdogService = new Thread(new ModuleWatchdog());
                    watchdogService.start();
                    
                    client.send(new TickEvent(config.getModuleName(), iteration.get()));
                    logger.log(Level.INFO, "Simulation step {0}", iteration.get());
                    
                    if(executeOnce.get()) break;
                }
            }
        }   
    }
    
    /**
     * Implements thread controlling liveness of modules.
     * Checks all modules that have state set false and if one of them is critical
     * terminate simulation. If modules with false state are not critical they 
     * are added to ignored modules list (program will not wait for the response
     * from them anymore).
     */
    private class ModuleWatchdog implements Runnable {

        @Override
        public void run() {
           
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                System.out.println("Killed");
                return;
            }
            
            for(String moduleName : modules) {
                if(!moduleStates.get(moduleName)) {
                    if(moduleCriticalness.get(moduleName)) {
                        logger.log(Level.SEVERE, "Critical module {0} is "
                                + "not responding. Terminating simulation", moduleName);
                        terminateServer();
                        return; // Just for you, reader, to understand that it is the end.
                    }
                    else {
                        moduleIgnored.put(moduleName, true);
                    }
                }
            } 
            readyForTick.set(true);
        }   
    }
    
    public SimulationServer() 
    {   
        stopped.set(true);
        
        config = NetworkConfig.defaultConfig().getModuleConfigs().get(1);
        
        String modulesInfo = "Available modules:\n";
        for(ModuleConfig module : NetworkConfig.defaultConfig().getModuleConfigs()) {
            if(module.getModuleName() != config.getModuleName()) {
                modules.add(module.getModuleName());
                moduleCriticalness.put(module.getModuleName(), module.isCritical());
                modulesInfo += " * " + module.getModuleName() + (module.isCritical() ? "(critical)\n" : "\n");
            }
        }
        logger.log(Level.INFO, modulesInfo);
               
        iterationTime = (int)config.getOption("iterationTime");
        moduleResponseTimeout = (int)config.getOption("moduleResponseTimeout");
        
        iteration.set(0);
              
        logger.log(Level.INFO, "Starting communication client...");
        client = new ClientModule(config);
        try {
            client.connect();
        } catch (IOException ex) {
            Logger.getLogger(SimulationServer.class.getName()).log(Level.SEVERE, 
                    "Failed to connect to the communication server: {0}", ex.getMessage());
            System.exit(1);
        }

        mainThread = new Thread(this);
        tickService = new Thread(new TickTask());
        watchdogService = new Thread(new ModuleWatchdog());
    }
    
    /**
     * Starts simulation server.
     */
    public void start() {
        logger.log(Level.INFO, "Starting main loop...");
        mainThread.start();
        logger.log(Level.INFO, "Main loop started.");
    }
    
    /**
     * Interruption causes return!
     */
    @Override
    public void run()
    {
        SimulationEvent event;
        
        // Used auxiliary variable beacause interruptions are used for poll interrupting
        while(!Thread.interrupted()) {
            try {
                //event = client.getIncomingQueue().poll(pollTimeout, TimeUnit.MILLISECONDS);
                event = client.getIncomingQueue().take();
                logger.log(Level.INFO, "Received event, type: {0}", event.getClass().getSimpleName());
                handleEvent(event);
            } catch (InterruptedException ex) {
                break;
            }
        }
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
        watchdogService.interrupt();
        try {
            while(tickService.isAlive() || watchdogService.isAlive()) {
                if(System.nanoTime() > t0 + 5*10e9) {
                    throw(new Exception("Can not pause simulation!"));
                }
            }
            logger.log(Level.INFO, "Simulation paused.");
        }
        catch(Exception e) {
            handlePauseTimeoutException(e);
        }
    }
    
    /**
     * Handles module wakeup.
     * If the module was restarted and started responding for ticks correctly 
     * (with the right iteration number in CycleCompletedEvent) it is removed 
     * from ignored modules list.
     * @param event CycleCompleted event
     */
    private void checkIgnoredModules(CycleCompletedEvent event) 
    {
        if(event.iteration() == iteration.get()) {
            moduleIgnored.put(event.sender(), false);
        }
    }
      
    /**
     * Checks if next iteration can be done.
     * The condition is that status of all modules is set true or modules with
     * status false are not critical.
     */
    private void checkNextIterationClearance() 
    {            
        for(Entry<String, Boolean> entry : moduleStates.entrySet()) {
            if(!entry.getValue() && !moduleIgnored.get(entry.getKey())) {
                return;
            }
        }
        
        watchdogService.interrupt();
        readyForTick.set(true);
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
                
        logger.log(Level.SEVERE, "Stopping main loop thread...");
        mainThread.interrupt();
        
        logger.log(Level.INFO, "Waiting for main loop thread to die...");
        while(mainThread.isAlive()) {}
        
        logger.log(Level.INFO, "Disconnectiong client...");
        // Disconnecting client
        client.close();
        
        System.exit(1);       
    }

    /**
     * Stops simulation environment.
     */
    public void terminateServer() 
    {
        logger.log(Level.INFO, "Sending termination signal to modules...");
        client.send(new TerminateSimulationEvent(config.getModuleName()));

        logger.log(Level.INFO, "Shutting down processes...");
        tickService.interrupt();
        watchdogService.stop();
        mainThread.stop();
                        
        while(tickService.isAlive()) {}
        logger.log(Level.INFO, "Tick service interrupted");
        while(watchdogService.isAlive()) {}
        logger.log(Level.INFO, "Module watchdog service interrupted");
        while(mainThread.isAlive()) {}
        logger.log(Level.INFO, "Main thread interrupted");
        
        logger.log(Level.INFO, "Disconnectiong client...");
        client.close();
        
        // HAPPY END
        System.exit(0);
    }
    
    /**
     * Main method for application.
     * @param args No parameters are expected.
     */
    public static void main(String[] args)
    {
        SimulationServer app = new SimulationServer();
        app.start();
        System.out.println("Naciśnij enter any zakończyć.");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(SimulationServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        app.terminateServer();
        
    }
}
