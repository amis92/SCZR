package burtis.modules.sync;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import burtis.common.events.TerminateSimulationEvent;
import burtis.common.events.Sync.TickEvent;
import burtis.modules.AbstractNetworkModule;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;

/**
 * Synchronization source for all time-dependent modules along with
 * communication server.
 * 
 * Sends {@link TickEvent} according to the internal state.
 * 
 * @author Mikołaj Sowiński
 *
 */
public class SynchronizationModule extends AbstractNetworkModule
{
    private static final long INITIAL_PERIOD = 1000l;
    private static final Logger logger = Logger
            .getLogger(SynchronizationModule.class.getName());
    private volatile boolean isRunning = false;
    /**
     * Number of iterations.
     */
    private AtomicLong iteration = new AtomicLong(0);
    /**
     * Ticking service.
     */
    private TickService tickService;

    public long getIteration()
    {
        return iteration.get();
    }

    public ModuleConfig getModuleConfig()
    {
        return moduleConfig;
    }

    protected long nextIteration()
    {
        return iteration.getAndIncrement();
    }

    public void startSimulation()
    {
        if (!isRunning)
        {
            isRunning = true;
            tickService.start(INITIAL_PERIOD);
        }
        else
        {
            logger.warning("Start error: Simulation already started!");
        }
    }
    
    private void doPause()
    {
        logger.info("Pausing simulation...");
        isRunning = false;
        tickService.stop();
        Module.interruptModuleWatchdog();
    }

    public void pauseSimulation()
    {
        if (isRunning)
        {
            doPause();
        }
        else
        {
            logger.warning("Pausing error: Simulation is not running.");
        }
    }

    protected void doStep()
    {
        doPause();
        logger.info("Doing one step of simulation.");
        tickService.tickOnce();
    }

    @Override
    protected void init()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Modules:\n========");
        for (ModuleConfig module : NetworkConfig.defaultConfig()
                .getModuleConfigs())
        {
            if (module.getModuleName().equalsIgnoreCase(
                    moduleConfig.getModuleName()))
            {
                continue;
            }
            Module.add(module.getModuleName(), module.isCritical());
            builder.append(" * " + module.getModuleName());
            builder.append(module.isCritical() ? " (critical)\n" : "\n");
        }
        logger.info(builder.toString());
        tickService = new TickService(moduleConfig.getModuleName(),
                iteration::incrementAndGet, this::send);
        Module.resetModuleWatchdog();
    }

    @Override
    protected void terminate()
    {
        logger.info("Sending termination signal to modules...");
        send(new TerminateSimulationEvent(moduleConfig.getModuleName()));
        logger.info("Shutting down processes...");
        tickService.stop();
        Module.stopModuleWatchdog();
    }

    public SynchronizationModule(ModuleConfig config)
    {
        super(config);
    }

    /**
     * Main method for application.
     * 
     * @param args
     *            No parameters are expected.
     */
    public static void main(String[] args)
    {
        SynchronizationModule app = new SynchronizationModule(NetworkConfig
                .defaultConfig().getModuleConfigs()
                .get(NetworkConfig.SYNC_MODULE));
        app.eventHandler = new SyncEventHandler(app);
        app.main();
    }
}
