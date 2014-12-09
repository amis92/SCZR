package burtis.modules.passengers;

import burtis.common.events.ConfigurationEvent;
import burtis.common.events.CycleCompletedEvent;
import burtis.common.events.ModuleReadyEvent;
import burtis.common.events.PassengerInfoRequestEvent;
import burtis.common.events.Passengers.BusStopsListRequestEvent;
import burtis.common.events.Passengers.WaitingPassengersEvent;
import burtis.common.events.Simulation.BusArrivesAtBusStopEvent;
import burtis.common.events.Simulation.BusStopsListEvent;
import burtis.common.events.Simulation.WaitingPassengersRequestEvent;
import burtis.common.events.SimulationEvent;
import burtis.common.events.TerminateSimulationEvent;
import burtis.common.events.Sync.TickEvent;
import burtis.common.events.gui.PassengerGenerationRateConfigurationEvent;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.client.ClientModule;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Module responsible for all passengers operations.
 * 
 * @author Mikołaj Sowiński
 */
public class PassengerModule {
    
    private final ClientModule client;
    private final BlockingQueue<SimulationEvent> eventQueue;
    
    private static final ModuleConfig simulationModuleConfig = 
            NetworkConfig.defaultConfig().getModuleConfigs().get(NetworkConfig.SIM_MODULE);
    private static final ModuleConfig passengerModuleConfig = 
            NetworkConfig.defaultConfig().getModuleConfigs().get(NetworkConfig.PSNGR_MODULE);

    public static final Logger logger = Logger.getLogger(passengerModuleConfig.getModuleName());
    
    /**
     * Current cycle.
     * Positive value mens in cycle, negative in between.
     */
    private long currentCycle;
   
    public PassengerModule() {
                
        client = new ClientModule(passengerModuleConfig);
        try {
            client.connect();
        } catch (IOException ex) {
            handleError(Level.SEVERE, ex.getMessage());
        }
        
        eventQueue = client.getIncomingQueue();
        
    }
    
    /**
     * Handles errors.
     * If error level is Level.SEVERE stop method is executed and module 
     * is terminated.
     * @param level error level (class Level)
     * @param message error message
     */
    private void handleError(Level level, String message) {
        
        logger.log(level, message);
        if(level == Level.SEVERE) {
            stop();
        }
        
    }

    /**
     * Wait for TickEvent and then for the simulation module to complete its 
     * iteration while responding to the simulation module requests.
     * Returns on TerminateSimulationEvent or on the TickEvent followed by
     * corresponding CycleCompletedEvent from simulation module. While waiting
     * responds to the simulation module's passenger state requests.
     */
    private void mainEventHandler() {
        
        eventQueue.forEach((SimulationEvent event) -> {
            
            // Handler for simulation termintaion
            if(event instanceof TerminateSimulationEvent) {
                logger.log(Level.INFO, "Termination event received from {0}.",
                        event.sender());
                stop();
            }
            
            // Handler for configuration events
            if(event instanceof ConfigurationEvent) {
                handleConfigurationEvent((ConfigurationEvent)event);
                eventQueue.remove(event);
            }
            
            // Processed PassengerInfoRequest event is removed when handled.
            else if(event instanceof PassengerInfoRequestEvent) {
                handleInfoRequest((PassengerInfoRequestEvent)event);
                eventQueue.remove(event);
            }
            
            // Tick event handler.
            else if(event instanceof TickEvent) {
                
                TickEvent tick = (TickEvent)event;
                
                // If there is TickEvent before CycleComleted then sth. is VERY wrong.
                if(currentCycle > 0 && tick.iteration() != currentCycle) {
                    handleError(Level.SEVERE, 
                            "Tick received before completion of cycle. Terminating.");
                }
                
                currentCycle = tick.iteration();
                eventQueue.remove(event);
                
            }
            
            // CycleCompleted handler.
            else if(event instanceof CycleCompletedEvent) {
                
                // It must originate from simulation module.
                if(event.sender().equals(simulationModuleConfig.getModuleName())) {
                    
                    // Check if it corresonds to the right cycle (not -> kill).
                    if(((CycleCompletedEvent)event).iteration() != currentCycle) {
                        handleError(Level.SEVERE, "Incorrect cycle number. Terminating.");
                    }
                    
                    eventQueue.remove(event);
                    List<SimulationEvent> events = new LinkedList<>();
                    eventQueue.drainTo(events);
                    iterate(events);
                }
                else {
                    eventQueue.remove(event);
                }
                
            }
            
            // Any other event.
            else {
                if(currentCycle < 0) {
                    logger.log(Level.WARNING, "Wrong time or unknown event, type: {0}. Event igored.",
                            event.getClass().getSimpleName());
                    eventQueue.remove(event);
                }
            }
            
        });
        
    }
    
    /**
     * Handles info requests from simulation module and GUI.
     */
    private void handleInfoRequest(PassengerInfoRequestEvent event) {
        
        if(event instanceof WaitingPassengersRequestEvent) {
            int busStopId = ((WaitingPassengersRequestEvent)event).getBusStopId();
            int waitingPassengers = BusStop.waitingPassengers(busStopId);
            client.send(new WaitingPassengersEvent(
                    passengerModuleConfig.getModuleName(),
                    busStopId,
                    waitingPassengers));
        }
        
        // Unknown event handler.
        else {
            logger.log(Level.WARNING, "Unknown event, type: {0}. Event igored.",
                    event.getClass().getSimpleName());
        }
        
    }
    
    /**
     * Handles configuration events.
     * 
     * @param configurationEvent 
     */
    private void handleConfigurationEvent(ConfigurationEvent event) {
        
        if(event instanceof PassengerGenerationRateConfigurationEvent) {
            PassengerGenerationRateConfigurationEvent configEvent = 
                    (PassengerGenerationRateConfigurationEvent)event;
            Passenger.setGenerationCycleLength(configEvent.getGenerationCycleLength());
            Passenger.setPassengersPerCycle(configEvent.getPassengersPerCycle());
        }
        
        // Unknown event handler.
        else {
            logger.log(Level.WARNING, "Unknown event, type: {0}. Event igored.",
                    event.getClass().getSimpleName());
        }

    }
    
    /**
     * Computes new iteration.
     * Order of computation is as follows:
     * - handle all events given as an argument
     * - tick transactions (and create new)
     * - send results
     */
    private void iterate(List<SimulationEvent> events) {
        
        Passenger.generatePassengers();
        
        events.forEach((SimulationEvent event) -> {
            
            if(event instanceof BusArrivesAtBusStopEvent) {
                busArrives((BusArrivesAtBusStopEvent)event);
            }
            
            // Unknown event handler.
            else {
                logger.log(Level.WARNING, "Unknown event, type: {0}. Event igored.",
                        event.getClass().getSimpleName());
            }
            
            eventQueue.remove(event);
            
        });
        
        Transaction.tickTransactions();
        
        // Send all event that are to be sent.
        EventBuilder.getEvents(passengerModuleConfig.getModuleName())
                .forEach((SimulationEvent event) -> {
                    client.send(event);
                });
        
        currentCycle = -1;
                        
    }
    
    /**
     * Stops module.
     */
    private void stop() {
        logger.log(Level.INFO, "Terminating module...");
        client.close();
        logger.log(Level.INFO, "Terminated. Bye!");
        System.exit(0);
    }
        
    /**
     * Conducts start up sequence.
     * Sends BusStopsListRequestEvent to the simulation module and waits for the
     * BusStopsListEvent. When received sends a ModuleReadyEvent.
     * 
     * WARNING: All events received before BusStopsListEvent are ignored!
     */
    private void start() {
        
        SimulationEvent event;
        
        
        client.send(new BusStopsListRequestEvent(passengerModuleConfig.getModuleName()));
        
        while(true) {
            
            try {
                logger.log(Level.INFO, "Requesting bus stops list...(timeout 1min)");
                client.send(new BusStopsListRequestEvent());
                event = eventQueue.poll(1, TimeUnit.MINUTES);
                
                if(event instanceof TerminateSimulationEvent) {
                    logger.log(Level.INFO, "Termination event received from {0}.",
                            event.sender());
                    stop();
                }
                else if(event instanceof BusStopsListEvent) {
                    logger.log(Level.INFO, "Bus stops list received.");
                    BusStop.add(((BusStopsListEvent)event).getBusStops());
                    client.send(new ModuleReadyEvent(passengerModuleConfig.getModuleName()));
                    break;
                }
                else {
                    if(event != null) {
                        logger.log(Level.WARNING, 
                                "Event {0} ignored. Application in init state.", 
                                event.getClass().getSimpleName());
                        eventQueue.remove(event);
                    }
                }
                
            } catch (InterruptedException ex) {
                logger.log(Level.SEVERE, "Startup operation terminated. Terminating module.");
                stop();
            }
  
        }
    }
    
    /**
     * Handles BusArrivesAtBusStopEvent. 
     * If bus is not registered it is registered and put to the bus stop queue.
     * Otherwise it only goes to the right queue.
     */
    private void busArrives(BusArrivesAtBusStopEvent event) {
        
        Bus bus = Bus.getBus(event.getBusId());
        BusStop busStop = BusStop.getBusStop(event.getBusStopId());
        
        if(busStop == null) {
            logger.log(Level.SEVERE, "BusStop@{0} is unknown! Terminating.", event.getBusStopId());
            System.exit(1);
        }
        
        if(bus == null) {
            bus = Bus.add(event.getBusId());
        }
        
        bus.arrive();
        busStop.enqueueBus(bus);
        
    }
    
    public static void main(String[] args) {
        
        
        
        PassengerModule moduleApp = new PassengerModule();
        moduleApp.start();
        logger.log(Level.INFO, "Starting main event handler loop...");
        while(true) {
            moduleApp.mainEventHandler();
        }
        
    }

}
