package burtis.modules.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import burtis.common.events.flow.TerminateSimulationEvent;
import burtis.common.events.flow.TickEvent;
import burtis.modules.AbstractNetworkModule;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;

/**
 * Synchronisation source for all time-dependent modules along with a module
 * failure controller.
 * 
 * Sends {@link TickEvent} according to the internal state.
 * 
 * @author Mikołaj Sowiński
 * @author Amadeusz Sadowski
 */
public class SynchronizationModule extends AbstractNetworkModule
{
    private static final long INITIAL_PERIOD = 1000L;
    /**
     * Synchronisation module logger.
     */
    private static final Logger logger = Logger
            .getLogger(SynchronizationModule.class.getName());

    /**
     * Main method for application.
     * 
     * @param args
     *            No parameters are expected.
     */
    public static void main(String[] args)
    {
        NetworkConfig netConfig = NetworkConfig.defaultConfig();
        ModuleConfig moduleConfig = netConfig.getModuleConfigs().get(
                NetworkConfig.SYNC_MODULE);
        printNetworkConfig(netConfig, moduleConfig.getModuleName());
        SynchronizationModule app = new SynchronizationModule(moduleConfig);
        app.main();
    }

    /**
     * Creates list of modules from default network config, ignoring 'itself',
     * and provides it to {@link WatchdogService}'s constructor.
     * 
     * @param syncConfig
     *            - 'itself'
     * @param shutdownAction
     *            - provided to service constructor.
     * @return Prepared service.
     */
    private static WatchdogService createWatchdogService(
            ModuleConfig syncConfig, Runnable shutdownAction)
    {
        List<ModuleConfig> configs = NetworkConfig.defaultConfig()
                .getModuleConfigs();
        List<WatchedModule> modules = new ArrayList<>(configs.size());
        for (ModuleConfig moduleConfig : configs)
        {
            if (moduleConfig.getModuleName().equalsIgnoreCase(
                    syncConfig.getModuleName()))
            {
                continue;
            }
            modules.add(new WatchedModule(moduleConfig));
        }
        return new WatchdogService(shutdownAction, modules);
    }

    /**
     * Lists modules to console, describing which are critical. Omits itself.
     * 
     * @param netConfig
     *            - list of all modules.
     * @param moduleName
     *            - to know itself.
     */
    private static void printNetworkConfig(NetworkConfig netConfig,
            String moduleName)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("\nModules:\n========\n");
        for (ModuleConfig module : netConfig.getModuleConfigs())
        {
            if (module.getModuleName().equalsIgnoreCase(moduleName))
            {
                continue;
            }
            builder.append(" * " + module.getModuleName());
            builder.append(module.isCritical() ? " (critical)\n" : "\n");
        }
        logger.info(builder.toString());
    }

    private volatile boolean isRunning = false;
    /**
     * Number of iterations.
     */
    private final AtomicLong iteration = new AtomicLong(0);
    /**
     * Ticking service.
     */
    private final TickService tickService;
    private final WatchdogService watchdogService;

    public SynchronizationModule(ModuleConfig config)
    {
        super(config);
        this.watchdogService = createWatchdogService(config, this::shutdown);
        this.eventHandler = new SyncEventHandler(this, watchdogService);
        this.tickService = new TickService(moduleConfig.getModuleName(),
                iteration::incrementAndGet, this::send, watchdogService);
    }

    public void doStep()
    {
        doPause();
        logger.info("Doing one step of simulation.");
        tickService.tickOnce();
    }

    public long getIteration()
    {
        return iteration.get();
    }

    public void pauseSimulation()
    {
        logger.info("Pausing simulation.");
        if (isRunning)
        {
            doPause();
        }
        else
        {
            logger.warning("Pausing error: Simulation is not running.");
        }
    }

    public void startSimulation()
    {
        logger.info("Starting simulation.");
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
        isRunning = false;
        tickService.stop();
    }

    @Override
    protected void init()
    {
    }

    protected long nextIteration()
    {
        return iteration.getAndIncrement();
    }

    @Override
    protected void terminate()
    {
        logger.info("Shutting down processes.");
        tickService.stop();
        watchdogService.stopWatching();
        logger.info("Sending termination signal to modules.");
        send(new TerminateSimulationEvent(moduleConfig.getModuleName()));
    }
}
