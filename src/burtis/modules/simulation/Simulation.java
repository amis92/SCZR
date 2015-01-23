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
 * Simulation module. </br></br>
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
     * Main method for application.
     * 
     * @param args
     *            No parameters are expected.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        Simulation app = new Simulation(NetworkConfig.defaultConfig());
        app.main();
    }

    /**
     * Action executor responsible for sending events to other modules.
     */
    private final ActionExecutor actionExecutor;
    /**
     * Bus manager.
     */
    private final BusManager busManager;
    /**
     * Bus stop manager.
     */
    private final BusStopManager busStopManager;
    /**
     * Current iteration.
     * 
     * This value is initially -1 because it <b>must</b> always be one less then
     * iteration number received in {@link TickEvent} and in first iteration
     * this number is equal to 0.
     */
    private long currentIteration;
    /**
     * Depot.
     */
    private final Depot depot;
    /**
     * Simulation logger.
     */
    private final Logger logger = Logger.getLogger(Simulation.class.getName());

    /**
     * Creates a new simulation.
     * 
     * @param netConfig
     *            network configuration
     * @throws Exception
     */
    private Simulation(NetworkConfig netConfig) throws Exception
    {
        super(netConfig.getModuleConfigs().get(NetworkConfig.SIM_MODULE));
        this.actionExecutor = new ActionExecutor(this.client, netConfig);
        this.depot = new Depot();
        this.busStopManager = new BusStopManager(
                SimulationModuleConsts.getDefaultBusStops(), depot, logger);
        this.busManager = new BusManager(busStopManager,
                SimulationModuleConsts.NUMBER_OF_BUSES, depot, logger);
        this.eventHandler = new SimulationEventHandler(this, actionExecutor,
                busManager, busStopManager, depot, logger);
        this.currentIteration = 0;
        // only for debugging
        // addFileLoggerHandler();
    }

    public long getCurrentCycle()
    {
        return currentIteration;
    }

    public Logger getLogger()
    {
        return logger;
    }

    public void setCurrentCycle(long currentCycle)
    {
        this.currentIteration = currentCycle;
    }

    /**
     * Sends termination event and shuts down module loop, closing all
     * operations.
     */
    @Override
    public void shutdown()
    {
        send(new TerminateSimulationEvent(this.moduleConfig.getModuleName()));
        super.shutdown();
    }

    /**
     * Adds dumping logs to file.
     * 
     * @throws Exception
     *             when the filehanler couldn't be initialized or added to
     *             logger.
     */
    @SuppressWarnings("unused")
    private void addFileLoggerHandler() throws Exception
    {
        Handler handler;
        try
        {
            handler = new FileHandler(this.getClass().getName() + ".log");
            handler.setFormatter(new LogFormatter());
            logger.addHandler(handler);
        }
        catch (SecurityException | IOException e)
        {
            logger.severe(e.getMessage());
            throw e;
        }
    }

    @Override
    protected void init()
    {
        actionExecutor.sendModuleReadyEvent(currentIteration);
    }

    @Override
    protected void terminate()
    {
        logger.log(Level.INFO, "Terminating module...");
    }
}
