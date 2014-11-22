/**
 * 
 */
package burtis.modules.sync;

import burtis.modules.common.ModuleConfig;
import burtis.common.events.ChangeSimulationModeEvent;
import burtis.common.events.ChangeSimulationModeEvent.State;
import burtis.common.events.ChangeSimulationSpeedEvent;
import burtis.common.events.CycleCompletedEvent;
import burtis.common.events.ErrorEvent;
import burtis.common.events.SimulationEvent;
import burtis.common.events.SlowDownEvent;
import burtis.common.events.TerminateSimulationEvent;
import burtis.common.events.TickEvent;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.client.Client;
import burtis.modules.network.server.Server;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import order.ServerOrder;


/**
 * Synchronization source for all time-dependent modules along with communication server.
 * 
 * Sends {@link TickEvent} events to all modules as CLK client according to the internal mode set.
 * Server is to be controlled by {@link ServerOrder}s. 
 * 

 * 
 * @author Mikołaj Sowiński
 *
 */
public class SimulationServer implements Runnable
{
    
    private static final Logger logger = Logger.getLogger(SimulationServer.class.getName());
    
    //private final Server server;
    private final Client<SimulationEvent> client;
    
    /**
     * Do we wait for a trigger to start new iteration? By default set true.
     */
    private boolean waitForTrigger = true;
    
    /**
     * Sync module configuration.
     */
    private final SyncConfig config;
    
    /**
     * Main execution loop thread.
     */
    private final Thread mainThread;
    
    /**
     * Thread that calls at the time when new iteration should start.
     */
    private final ScheduledExecutorService tickAlarm = Executors.newSingleThreadScheduledExecutor();
    
    /**
     * Iteration start time.
     */
    private long iterStartTime = 0;
    
    /**
     * Stop simulation at next significant event.
     */
    private boolean stopAtNextEvent = false;
    
    /**
     * Stops simulation at every iteration.
     */
    private boolean stepMode = false;
    
    /**
     * Simulation stopped.
     */
    private boolean stopped = false;
    
    /**
     * Terminates main thread.
     */
    AtomicBoolean stopMainThread = new AtomicBoolean();

    /**
     * Ready for next tick.
     */
    private boolean readyForTick;
        
    public SimulationServer() 
    {        
        config = SyncConfig.defaultConfig();
        stopMainThread.set(false);
        
        logger.log(Level.INFO, "Starting communication server...");
        // Start communication server
        //server = new Server(new NetworkConfig("127.0.0.1", config.getNetworkServerModuleConfig()));
        //server.run();
       
        logger.log(Level.INFO, "Starting client...");
        // Create and connect client
        client = new Client<>("127.0.0.1", config.getPort());
        client.connect();
        
        logger.log(Level.INFO, "Starting main loop...");
        // Start main loop
        mainThread = new Thread(this);
        mainThread.run();         
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        new SimulationServer();
    }

    /**
     * Interruption causes return!
     */
    @Override
    public void run()
    {
        SimulationEvent event;
        
        
        // Used auxiliary variable beacause interruptions are used for poll interrupting
        while(!stopMainThread.get()) {
            
            // THIS MUST BE THE FIRST
            if(!stopped && readyForTick && !waitForTrigger) tick();
                        
            // Wait for event
            try {
                
                // THE ONLY BLOCKING ALLOWED HERE
                event = client.getEventsBlockingQueue()
                        .poll(config.getModuleResponseTimeout(), TimeUnit.MILLISECONDS);
                
                // !! NO BLOCKING BEYOND THIS POINT
                                
            } catch (InterruptedException e) {
                continue;
            }
            
            // Timed out
            if(event == null) {
                timeout();
            }
            else {
                handleEvent(event);
            }            
        }
    }

    /**
     * Sets all modules waiting value true and sends TickEvent.
     */
    private void tick() {
        
        readyForTick = false;
        
        for(ModuleConfig module : config.getModuleConfigs()) {
            module.setFinished(false);
        }

        client.send(new TickEvent(config.getName()));
        iterStartTime = System.nanoTime();
    }
    
    /**
     * Handles poll timeout.
     * This is important to detect module failures.
     * 
     * @return boolean should cause stop at triggering?
     */
    private boolean timeout() {
        
        boolean willStop = false;
        String culprits = "";
        
        // Check for who we are waiting
        for(ModuleConfig module : config.getModuleConfigs()) {
            
            if(!module.isFinished()) {
                // Culprit!
                if(module.isCritical()) {
                    willStop = true;
                    culprits = culprits + " " + module.getModuleName();
                }
                else {
                    // If it's not critical ignore it
                    module.setIgnore(true);
                }
            }
            
        }
        
        if(willStop) {
            client.send(new ErrorEvent(config.getName(), "Critical modules (" 
                    + culprits + ") failed to meet timing criteria"));
            // This is FATAL
            terminateServer();
            return true; // Actually doesn't matter...
        }
        else {
            return false;
        }
    }
    
    /**
     * Provides handling various events.
     * 
     * @param event event to be handled
     */
    private void handleEvent(SimulationEvent event) 
    {
        String eventName = event.getClass().getSimpleName();
        switch(eventName) {
            
            case "CycleCompletedEvent":
                cycleCompleted(event);
                break;
                
            case "StartSimulationEvent":
                waitForTrigger = false;
                stopped = false;
                break;
                
            case "StopSimulationEvent":
                stopped = true;
                break;
                
            case "ChangeSimulationSpeedEvent":
                config.setIterationTime(((ChangeSimulationSpeedEvent)event).speed());
                break;
            
            case "ChangeSimulationModeEvent":
                changeSimulationMode(event);
                break;
            
            case "TerminateSimulationEvent":
                terminateServer();
                logger.log(Level.SEVERE, "Simulation terminated by {0}", event.sender());
                break;
                
            default:
                logger.log(Level.WARNING, "Unhandled event: {0}", eventName);                
        }
        
    }
    
    private void changeSimulationMode(SimulationEvent event) {
        
        State targetState = ((ChangeSimulationModeEvent)event).getTargetState();
        
        switch(targetState) {
            case STOPPED:
                stopped = true;
                break;
            
            case NEXTEVENT:
                stopAtNextEvent = true;
                stepMode = false;
                stopped = false;
                break;
                
            case STEPMODE:
                stepMode = true;
                stopped = false;
                stopAtNextEvent = false;
                break;
                
            case RUNNING:
                stepMode = false;
                stopped = false;
                stopAtNextEvent = false;
                break;
                
        }
    }
      
    /**
     * Dedicated handler for CycleCompletedEvent.
     */
    private void cycleCompleted(final SimulationEvent event) {
        
        String sender = ((CycleCompletedEvent)event).sender();
        boolean anyLeftWaiting = false;
        
        // Setting waiting value for appropriate module
        for(ModuleConfig module : config.getModuleConfigs()) {            
            if(module.getModuleName().equals(sender)) {
                module.setFinished(true);
            }
            if(!module.isFinished()) anyLeftWaiting = true;
        }
        
        if(!anyLeftWaiting) {
            checkTiming();
            checkStopAtSignificantEvent(event);
        }       
    }

    private void checkStopAtSignificantEvent(SimulationEvent event) {
        // If we want to stop at next significant event
        if(((CycleCompletedEvent)event).significant() && stopAtNextEvent) {
            waitForTrigger = true;
            logger.log(Level.INFO, "Simulation stopped due to significant event from {0}" , event.sender());
        }
    }
    
    /**
     * Checks if iteration took less than set maximal iteration time (if we 
     * are ready for the next tick).
     * If iteration took more than iteration time set in configuration warning
     * will be logged and configured time will be adjusted to 1.2 measured
     * iteration time.
     * If it took less, an alarm is set to make it ready to tick in specified time.
     */
    private void checkTiming() {
        
        long iterationTime = System.nanoTime()-iterStartTime;
        
        if( iterationTime > config.getIterationTime()) { 
           config.setIterationTime((long)(iterationTime*1.2));
           client.send(new SlowDownEvent(config.getName(), (long)(iterationTime*1.2)));
           logger.log(Level.WARNING, "Simulation slowed down. New max. iteration time: {0}", (long)(iterationTime*1.2));
           readyForTick = true;
        }
        
        // If there is any time left to wait
        else {
            // If we are not in the step mode
            if(!stepMode) {
                long timeLeft = config.getIterationTime() - iterationTime;
                tickAlarm.schedule(this::tickAlarmFunction, timeLeft, TimeUnit.NANOSECONDS);
            }
        }
    }
    
    /**
     * Handler for tick alarm.
     */
    private void tickAlarmFunction() {
        readyForTick = true;
        mainThread.interrupt();
    }

    /**
     * Stops simulation and server.
     */
    public void terminateServer() {
        
        // Send termination signal to modules.
        logger.log(Level.INFO, "Sending termination signal to modules...");
        client.send(new TerminateSimulationEvent(config.getName()));

        logger.log(Level.INFO, "Shutting down tick alarm...");
        tickAlarm.shutdownNow();
        while(!tickAlarm.isTerminated()) {}
        
        logger.log(Level.INFO, "Stopping main loop thread...");
        // Stop main loop thread
        stopMainThread.set(true);
        mainThread.interrupt();
        
        logger.log(Level.INFO, "Waiting for main loop thread to die...");
        while(mainThread.isAlive()) {}

        logger.log(Level.INFO, "Disconnectiong client...");
        // Disconnecting client
        client.closeConnection();
          
        logger.log(Level.INFO, "Stopping communication server...");
        // Terminating server
        //server.stop();
        
        // HAPPY END
        System.exit(0);      
        
    }
}
