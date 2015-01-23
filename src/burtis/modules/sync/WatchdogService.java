package burtis.modules.sync;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.modules.sync.WatchedModule.State;

/**
 * Implements thread controlling liveness of modules. Checks all modules that
 * have state set false and if one of them is critical terminate simulation. If
 * modules with false state are not critical they are added to ignored modules
 * list (program will not wait for the response from them anymore). </br> The
 * check is performed if either happens:
 * <ul>
 * <li>{@link #acceptTick()} is called after scheduled period was reached;</li>
 * <li>scheduled period was reached again and no {@link #acceptTick()} call was
 * done in between.</li>
 * </ul>
 * 
 * @author Amadeusz Sadowski
 */
class WatchdogService
{
    private static final Logger logger = Logger.getLogger(WatchdogService.class
            .getName());
    /**
     * Defines how often are modules checked.
     */
    private static final long PERIOD = 500000;
    private ScheduledExecutorService executor = Executors
            .newSingleThreadScheduledExecutor();
    private final AtomicBoolean isWatchTime = new AtomicBoolean(false);
    private final List<WatchedModule> modules;
    private final Runnable shutdownAction;

    public WatchdogService(Runnable shutdownAction, List<WatchedModule> modules)
    {
        this.modules = modules;
        this.shutdownAction = shutdownAction;
    }

    /**
     * If check was scheduled, it's executed.
     */
    public void acceptTick()
    {
        if (isWatchTime.compareAndSet(true, false))
        {
            check();
        }
    }

    /**
     * Find's appropriate {@link WatchedModule} and calls it's
     * {@link WatchedModule#setStateReady()}.
     * 
     * @param moduleName
     *            - module name to be found and handled.
     */
    public void handleModuleResponded(String moduleName, boolean isSynced)
    {
        WatchedModule module = findByName(moduleName);
        if (module == null)
        {
            logger.warning("Unknown module responded: " + moduleName);
            return;
        }
        if (isSynced)
        {
            module.setStateReady();
            logger.info("Response received from module: " + moduleName);
        }
        else
        {
            handleModuleOutOfSync(module);
        }
    }

    public void startWatching()
    {
        stopWatching();
        executor.scheduleAtFixedRate(this::scheduledCheck, 0, PERIOD,
                TimeUnit.MILLISECONDS);
    }

    public void stopWatching()
    {
        executor.shutdownNow();
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Checks state of every module.
     */
    private void check()
    {
        for (WatchedModule module : modules)
        {
            if (module.getState() == State.WAITING)
            {
                handleModuleOutOfSync(module);
            }
        }
    }

    private WatchedModule findByName(String moduleName)
    {
        for (WatchedModule watchedModule : modules)
        {
            if (watchedModule.getModuleName().equalsIgnoreCase(moduleName))
            {
                return watchedModule;
            }
        }
        logger.warning("No such module: " + moduleName);
        return null;
    }

    /**
     * Calls {@link WatchdogService#shutdownAction} if module is critical. In
     * other case, module's {@link WatchedModule#setIgnoredTrue()} is called.
     * 
     * @param module
     *            - it's state is checked.
     */
    private void handleModuleOutOfSync(WatchedModule module)
    {
        if (!module.isCritical())
        {
            if (!module.isIgnored())
            {
                module.setIgnoredTrue();
                logger.log(Level.INFO, "Module {0} is not responding.",
                        module.getModuleName());
            }
        }
        else
        {
            logger.log(
                    Level.SEVERE,
                    "Critical module {0} is not responding. Terminating simulation.",
                    module.getModuleName());
            shutdownAction.run();
        }
    }

    /**
     * Schedules next check. If a check was already scheduled, it's called
     * directly.
     */
    private void scheduledCheck()
    {
        if (!isWatchTime.compareAndSet(false, true))
        {
            // a check was already scheduled and not run
            check();
        }
    }
}
