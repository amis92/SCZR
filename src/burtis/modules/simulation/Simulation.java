package burtis.modules.simulation;

import burtis.common.constants.SimulationModuleConsts;
import burtis.common.events.Passengers.BusDepartEvent;
import burtis.common.events.Passengers.BusStopsListRequestEvent;
import burtis.common.events.Simulation.BusStopsListEvent;
import burtis.common.events.SimulationEvent;
import burtis.common.events.TerminateSimulationEvent;
import burtis.common.events.Sync.TickEvent;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.client.ClientModule;
import burtis.modules.simulation.models.Bus;
import burtis.modules.simulation.models.BusStop;
import burtis.modules.simulation.models.Terminus;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simulation module.
 * 
 * Contains depot, terminus, buses and bus stops. No passengers here. Communicates
 * with passenger module to obtain information on passengers waiting at bus stops.
 * 
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 *
 */
public class Simulation 
{

    public static final ModuleConfig simulationModuleConfig = 
            NetworkConfig.defaultConfig().getModuleConfigs().get(NetworkConfig.SIM_MODULE);
    public static final ModuleConfig passengerModuleConfig = 
            NetworkConfig.defaultConfig().getModuleConfigs().get(NetworkConfig.PSNGR_MODULE);
    
    public static final ClientModule client = new ClientModule(simulationModuleConfig);
    public static final BlockingQueue<SimulationEvent> eventQueue = client.getIncomingQueue();
    
    public static final Logger logger = Logger.getLogger(simulationModuleConfig.getModuleName());
    
    /**
     * Current cycle.
     * Positive value mens in cycle, negative in between.
     */
    private static long currentCycle;
    
    /**
     * Number of buses in the system.
     */
    private final int numberOfBuses = SimulationModuleConsts.NUMBER_OF_BUSES;
        
    /**
     * Bus capacity.
     */
    private final int busCapacity = SimulationModuleConsts.BUS_CAPACITY;
    
    /**
     * Bus starting frequency.
     * Given in number of simulation iterations.
     */
    private int busStartInterval = SimulationModuleConsts.BUS_START_INTERVAL;

    public static long getCurrentCycle() {
        return currentCycle;
    }
    
    /**
     * Creates a new simulation.
     */
    public Simulation() {
        
        try {
            client.connect();
            // Create buses at depot (default location)
            for(int i=0; i<numberOfBuses; i++) {
                Bus.add(busCapacity);
            }
            // Create bus stops
            BusStop.add(new Terminus(0,"Bielańska"));
            BusStop.add(30, "Plac Zamkowy");
            BusStop.add(60, "Hotel Bristol");
            BusStop.add(90, "Uniwersytet");
            BusStop.add(120, "Ordynacka");
            BusStop.add(150, "Foksal");
            BusStop.add(180, "Plac Trzech Krzyży");
            BusStop.add(210, "Plac na Rozdrożu");
            BusStop.add(240, "Plac Unii Lubelskiej");
            BusStop.add(270, "Rakowiecka");
            BusStop.add(new Terminus(300,"Bielańska"));
        } catch (IOException ex) {
            handleError(Level.SEVERE, ex.getMessage());
        }

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
     * Handles tick event.
     * 
     * @param tick TickEvent
     */
    private void tickEvent(TickEvent tick) {
    
        // If there is TickEvent before CycleComleted then sth. is VERY wrong.
        if(currentCycle > 0 && tick.iteration() != currentCycle) {
            handleError(Level.SEVERE, 
                    "Tick received before completion of cycle. Terminating.");
        }

        currentCycle = tick.iteration();
        
    }
    
    private void mainEventHandler() {
                
        while(true) {
            
            eventQueue.forEach((SimulationEvent event) -> {
                
                // Handler for simulation termintaion
                if(event instanceof TerminateSimulationEvent) {
                    logger.log(Level.INFO, "Termination event received from {0}.",
                            event.sender());
                    stop();
                }
            
                // Handler for BusStopsListRequest (init only)
                else if(event instanceof BusStopsListRequestEvent) {
                    logger.log(Level.INFO, "Bus stops list requested.");
                    client.send(new BusStopsListEvent(
                            simulationModuleConfig.getModuleName(), 
                            BusStop.getBusStopsList()));
                    eventQueue.remove(event);
                }
                
                // Tick event handler.
                else if(event instanceof TickEvent) {
                    tickEvent((TickEvent)event);
                    iterate();
                    eventQueue.remove(event);
                }
                
                // Bus depart event handler
                else if(event instanceof BusDepartEvent) {
                    busDepartEvent((BusDepartEvent)event);
                    eventQueue.remove(event);
                }   
            });
   
        }
 
    }
    
    private void iterate() {
        Bus.updatePositions();
        // Terminus.
    }
    
    /**
     * Handles BusDepartEvent.
     * @param event BusDepartEvent
     */
    private void busDepartEvent(BusDepartEvent event) {
        logger.log(Level.INFO, "Bus {0} departs from the bus stop.", event.getBusId());
        Bus bus = Bus.getBusById(event.getBusId());
        bus.depart();
        bus.setNextBusStop(BusStop.getBusStopById(event.getNextBusStopId()));
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
     * @param args
     */
    public static void main(String[] args)
    {
        Simulation app = new Simulation();
        while(true) {
            app.mainEventHandler();
        }
    }

}
