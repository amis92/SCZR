package burtis.modules.simulation;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.constants.SimulationModuleConsts;
import burtis.common.events.flow.TerminateSimulationEvent;
import burtis.common.events.flow.TickEvent;
import burtis.common.logging.LogFormatter;
import burtis.modules.AbstractNetworkModule;
import burtis.modules.network.NetworkConfig;
import burtis.modules.simulation.models.BusManager;
import burtis.modules.simulation.models.BusStopManager;
import burtis.modules.simulation.models.Depot;

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
    private final Logger logger = Logger.getLogger(Simulation.class
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
                depot,
                logger);
        
        this.eventHandler = new SimulationEventHandler(
                this, 
                actionExecutor, 
                busManager,
                busStopManager,
                depot,
                logger);
        
        this.currentIteration = 0;
        
        Handler handler;
        try
        {
            handler = new FileHandler(this.getClass().getName());
            handler.setFormatter(new LogFormatter());
            logger.addHandler(handler);
        }
        catch (SecurityException | IOException e)
        {
            logger.severe(e.getMessage());
            terminate();            
        }
        
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
     * Sends termination event and shuts down module loop, closing all operations.
     */
    @Override
    public void shutdown()
    {
        send(new TerminateSimulationEvent(this.moduleConfig.getModuleName()));
        super.shutdown();
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
    {
        actionExecutor.sendModuleReadyEvent(currentIteration);
    }
}
