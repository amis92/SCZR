package burtis.modules.sync;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class representing module.
 * 
 * @author Mikołaj
 */
public class Module
{
    /**
     * Dead module watchdog.
     */
    private static Thread watchdogService;
    /**
     * List of modules.
     */
    private static final List<Module> modules = new LinkedList<>();
    private static final SynchronizationModule sync = SynchronizationModule
            .getInstance();
    private final String moduleName;
    private final boolean critical;

    public enum State
    {
        WAITING, READY
    }

    private State state = State.WAITING;
    private boolean ignored = false;

    public Module(String moduleName, boolean critical)
    {
        this.moduleName = moduleName;
        this.critical = critical;
    }

    public static void add(String moduleName, boolean critical)
    {
        modules.add(new Module(moduleName, critical));
    }

    public static void resetModulesStates()
    {
        for (Module module : modules)
        {
            module.state = State.WAITING;
        }
    }

    public static void resetModuleWatchdog()
    {
        if (watchdogService == null)
        {
            watchdogService = new Thread(new ModuleWatchdog());
        }
        else if (watchdogService.isAlive())
        {
            watchdogService.interrupt();
            try
            {
                watchdogService.join();
                watchdogService = new Thread(new ModuleWatchdog());
            }
            catch (InterruptedException ex)
            {
                sync.getLogger().log(Level.SEVERE, ex.getMessage());
                sync.terminate();
            }
        }
        watchdogService.start();
    }

    public static Module getModuleByName(String moduleName)
    {
        for (Module module : modules)
        {
            if (module.moduleName == moduleName)
            {
                return module;
            }
        }
        return null;
    }

    /**
     * Sets ready and unignores module.
     * 
     * @param moduleName
     */
    public static void setReady(String moduleName)
    {
        Module module = getModuleByName(moduleName);
        if (module != null)
        {
            module.state = State.READY;
            if (module.ignored)
            {
                module.ignored = false;
                sync.getLogger().log(Level.INFO, "Unignored module {0}",
                        moduleName);
            }
        }
        else
        {
            sync.getLogger().log(Level.WARNING, "Unknown module {0}",
                    moduleName);
        }
    }

    public static boolean checkNextIterationClearance()
    {
        for (Module module : modules)
        {
            if (module.state == State.WAITING && !module.ignored)
            {
                return false;
            }
        }
        watchdogService.interrupt();
        return true;
    }

    public static void interruptModuleWatchdog()
    {
        watchdogService.interrupt();
    }

    public static void stopModuleWatchdog()
    {
        if (!watchdogService.isInterrupted())
        {
            watchdogService.interrupt();
        }
        try
        {
            if (watchdogService.isAlive())
                watchdogService.join();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(Module.class.getName()).log(Level.SEVERE,
                    ex.getMessage());
        }
    }

    private static class ModuleWatchdog implements Runnable
    {
        @Override
        public void run()
        {
        }
    }
}
