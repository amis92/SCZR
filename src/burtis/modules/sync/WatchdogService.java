package burtis.modules.sync;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.modules.sync.WatchedModule.State;

/**
 * Implements thread controlling liveness of modules. Checks all modules that
 * have state set false and if one of them is critical terminate simulation. If
 * modules with false state are not critical they are added to ignored modules
 * list (program will not wait for the response from them anymore).
 */
public class WatchdogService
{
    /**
     * Defines how often are modules checked.
     */
    private static final long PERIOD = 500;
    private static final Logger logger = Logger.getLogger(WatchdogService.class
            .getName());
    private ScheduledExecutorService executor = Executors
            .newSingleThreadScheduledExecutor();
    private final List<WatchedModule> modules;
    private final Runnable shutdownAction;

    public WatchdogService(Runnable shutdownAction, List<WatchedModule> modules)
    {
        this.modules = modules;
        this.shutdownAction = shutdownAction;
    }

    public void startWatching()
    {
        stopWatching();
        executor.scheduleAtFixedRate(this::watch, 0, PERIOD,
                TimeUnit.MILLISECONDS);
    }

    public void stopWatching()
    {
        executor.shutdownNow();
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void handleModuleResponded(String moduleName)
    {
        WatchedModule module = findByName(moduleName);
        if (module == null)
        {
            return;
        }
        module.setStateReady();
        logger.info("Response received from module: " + moduleName);
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

    private void watch()
    {
        for (WatchedModule module : modules)
        {
            if (module.getState() == State.WAITING)
            {
                handleWaitingForModule(module);
            }
        }
    }

    private void handleWaitingForModule(WatchedModule module)
    {
        if (!module.isCritical())
        {
            module.setIgnoredTrue();
            logger.log(Level.INFO, "Module {0} is not responding.",
                    module.getModuleName());
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
}
