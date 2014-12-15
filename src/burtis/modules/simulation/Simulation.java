package burtis.modules.simulation;

import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.constants.SimulationModuleConsts;
import burtis.common.events.SimulationEvent;
import burtis.common.events.flow.CycleCompletedEvent;
import burtis.common.events.passengers.WaitingPassengersEvent;
import burtis.common.events.simulation.BusMockupsEvent;
import burtis.common.mockups.MockupBus;
import burtis.modules.AbstractNetworkModule;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.passengers.PassengerModule;
import burtis.modules.simulation.models.Bus;
import burtis.modules.simulation.models.BusStop;
import burtis.modules.simulation.models.Depot;
import burtis.modules.simulation.models.Terminus;

/**
 * Simulation module.
 * 
 * Contains depot, terminus, buses and bus stops. No passengers here. Communicates
 * with passenger module to obtain information on passengers waiting at bus stops.
 * 
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 *
 */
public class Simulation extends AbstractNetworkModule
{
    
    private final static Simulation simulation = new Simulation(NetworkConfig.defaultConfig().getModuleConfigs().get(NetworkConfig.SIM_MODULE));

    private Logger logger;
    
    /**
     * Current cycle.
     * Positive value mens in cycle, negative in between.
     */
    private static long currentCycle;
    
    private int lineLength = 0;

    public int getLineLength() {
        return lineLength;
    }
       
    public Logger getLogger() {
        return logger;
    }
    
    public static Simulation getInstance() {
        return simulation;
    }
    
    public ModuleConfig getModuleConfig() {
        return moduleConfig;
    }

    public static long getCurrentCycle() {
        return currentCycle;
    }

    public static void setCurrentCycle(long currentCycle) {
        Simulation.currentCycle = currentCycle;
    }
    
    
    
    /**
     * Sends list of bus mockups ({@link MockupBus} to the {@link PassengerModule}.
     */
    public void sendBusMockups() {
        send(new BusMockupsEvent(
                moduleConfig.getModuleName(),
                new String[] { NetworkConfig.defaultConfig().getModuleConfigs().get(NetworkConfig.PSNGR_MODULE).getModuleName() }, 
                Bus.getMockups(),
                currentCycle));
    }
    
    /**
     * Sends CycleCompletedEvent and zeros currentCycle variable.
     */
    public void sendCycleCompleted() {
        send(new CycleCompletedEvent(
                moduleConfig.getModuleName(),
                currentCycle));
        currentCycle = 0;
    }
    
    /**
     * Creates a new simulation.
     */
    private Simulation(ModuleConfig config) {
        super(config); 
        logger = Logger.getLogger(moduleConfig.getModuleName());
    }
 
    @Override
    protected void init() {
                
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
        
        Bus bus;
        for(int i=0; i< SimulationModuleConsts.NUMBER_OF_BUSES; i++) {
            bus = Bus.add(SimulationModuleConsts.BUS_CAPACITY);
            Depot.putBus(bus);
        }
        
        lineLength = 300;
        
    }
    
    /**
     * Searches incoming event queue for WaitingPassengersEvent.
     * Event handler can not be used here as main thread is blocked in serial
     * execution of other event handler.
     * @return WaitingPassengersEvent or null if none is found
     */
    public WaitingPassengersEvent getWaitingPassengersEvent() {
        WaitingPassengersEvent waitingPassengersEvent;
        if(client.getIncomingQueue().size() > 0) {
            for(SimulationEvent event : client.getIncomingQueue()) {
                if(event instanceof WaitingPassengersEvent) {
                    waitingPassengersEvent = (WaitingPassengersEvent)event;
                    client.getIncomingQueue().remove(event);
                    return waitingPassengersEvent;
                }
            }
            return null;
        }
        else {
            return null;
        }
    }

    @Override
    protected void terminate() {
        logger.log(Level.INFO, "Terminating module...");
    }

    /**
     * Main method for application.
     * 
     * @param args
     *            No parameters are expected.
     */
    public static void main(String[] args)
    {
        Simulation app = Simulation.getInstance();
        app.eventHandler = new SimulationEventHandler();
        app.main();
    }

}