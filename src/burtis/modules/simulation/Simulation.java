package burtis.modules.simulation;

import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.constants.SimulationModuleConsts;
import burtis.common.events.SimulationEvent;
import burtis.common.events.Passengers.WaitingPassengersEvent;
import burtis.common.events.flow.TickEvent;
import burtis.common.mockups.MockupBus;
import burtis.modules.AbstractNetworkModule;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.passengers.PassengerModule;
import burtis.modules.simulation.models.Bus;
import burtis.modules.simulation.models.BusManager;
import burtis.modules.simulation.models.BusStop;
import burtis.modules.simulation.models.BusStopManager;
import burtis.modules.simulation.models.Depot;
import burtis.modules.simulation.models.Terminus;

/**
 * Simulation module.
 * 
 * Contains depot, terminus, buses and bus stops. No passengers here.
 * Communicates with passenger module to obtain information on passengers
 * waiting at bus stops.
 * 
 * @author Mikołaj Sowiński
 *
 */
public class Simulation extends AbstractNetworkModule
{
    
    /**
     * Simulation logger.
     */
    private static final Logger logger = Logger.getLogger(Simulation.class
            .getName());
    
    /**
     * Current iteration.
     * 
     * This value is initially -1 because it <b>must</b> always be one less then
     * iteration number received in {@link TickEvent} and in first iteration this
     * number is equal to 0.
     */
    private long currentIteration;
    
    /**
     * Action executor responsible for sending events to other modules.
     */
    private final ActionExecutor actionExecutor;
    
    /**
     * Bus stop manager.
     */
    private final BusStopManager busStopManager;
    
    /**
     * Depot.
     */
    private final Depot depot;
    
    /**
     * Bus manager.
     */
    private final BusManager busManager;

    /**
     * Creates a new simulation.
     * 
     * @param netConfig network configuration
     */
    private Simulation(NetworkConfig netConfig)
    {
        super(netConfig.getModuleConfigs().get(NetworkConfig.SIM_MODULE));
        
        this.actionExecutor = new ActionExecutor(
                this.client, 
                netConfig);
        
        this.depot = new Depot();
        
        this.busStopManager = new BusStopManager(
                SimulationModuleConsts.getDefaultBusStops(),
                depot);
        
        this.busManager = new BusManager(
                busStopManager,
                SimulationModuleConsts.NUMBER_OF_BUSES,
                SimulationModuleConsts.BUS_CAPACITY);
        
        this.eventHandler = new SimulationEventHandler(
                this, 
                actionExecutor, 
                busManager,
                busStopManager,
                depot);
        
        this.currentIteration = -1;
        
    }

    public Logger getLogger()
    {
        return logger;
    }

    public long getCurrentCycle()
    {
        return currentIteration;
    }

    public void setCurrentCycle(long currentCycle)
    {
        this.currentIteration = currentCycle;
    }

    /**
     * Sends list of bus mockups ({@link MockupBus} to the
     * {@link PassengerModule}.
     */
    public void sendBusMockups()
    {
        actionExecutor
                .sendBusMockupEvent(currentIteration, busManager.getMockups());
    }

    /**
     * Sends CycleCompletedEvent.
     */
    public void sendCycleCompleted()
    {
        actionExecutor.sendCycleCompletedEvent(currentIteration);
    }

    @Override
    protected void terminate()
    {
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
        Simulation app = new Simulation(NetworkConfig.defaultConfig());
        app.main();
    }

    @Override
    protected void init()
    {}
}
