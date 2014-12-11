package burtis.modules.sync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.modules.sync.Module.State;

/**
 * Implements thread controlling liveness of modules. Checks all modules that
 * have state set false and if one of them is critical terminate simulation. If
 * modules with false state are not critical they are added to ignored modules
 * list (program will not wait for the response from them anymore).
 */
public class WatchdogService
{
    private static final Logger logger = Logger.getLogger(WatchdogService.class
            .getName());
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public WatchdogService()
    {
        // TODO Auto-generated constructor stub
    }

    public void startWatching()
    {
        stopWatching();
        executor.execute(this::watch);
    }

    public void stopWatching()
    {
        executor.shutdownNow();
        executor = Executors.newSingleThreadExecutor();
    }

    private void watch()
    {
        try
        {
            Thread.sleep(5000);
        }
        catch (InterruptedException ex)
        {
            logger.info("Module watchdog interrupted.");
            return;
        }
        for (Module module : modules)
        {
            // We're still waiting for module?
            if (module.state == State.WAITING)
            {
                if (module.critical)
                {
                    logger.log(
                            Level.SEVERE,
                            "Critical module {0} is not responding. Terminating simulation",
                            module.moduleName);
                    // call shutdown
                }
                else
                {
                    module.ignored = true;
                }
            }
        }
    }
}
