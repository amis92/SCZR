package burtis.modules.sync;

import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.events.flow.CycleCompletedEvent;
import burtis.modules.network.ModuleConfig;

/**
 * Class representing watched module. Contains information how well synchronized
 * the module is, and if it already reported readiness in current iteration.
 * 
 * @author Mikołaj Sowiński
 */
class WatchedModule
{
    private static final Logger logger = Logger.getLogger(WatchedModule.class
            .getName());
    private final boolean isCritical;
    private boolean isIgnored = false;
    private final String moduleName;
    private State state = State.WAITING;

    public WatchedModule(ModuleConfig config)
    {
        this.moduleName = config.getModuleName();
        this.isCritical = config.isCritical();
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public State getState()
    {
        return state;
    }

    public boolean isCritical()
    {
        return isCritical;
    }

    public boolean isIgnored()
    {
        return isIgnored;
    }

    public void setIgnoredTrue()
    {
        this.isIgnored = true;
    }

    /**
     * Sets state as ready. Because of that, {@link #isIgnored} flag is reset to
     * <b>false</b>.
     */
    public void setStateReady()
    {
        state = State.READY;
        if (isIgnored)
        {
            isIgnored = false;
            logger.log(Level.INFO,
                    "Module {0} responding is sync, no longer ignored.",
                    moduleName);
        }
    }

    public void setStateWaiting()
    {
        this.state = State.WAITING;
    }

    /**
     * Describes module state known to {@link SynchronizationModule}.
     * 
     * @author Mikołaj Sowiński
     *
     */
    public enum State
    {
        /**
         * The module has already sent {@link CycleCompletedEvent} and is ready
         * for next tick.
         */
        READY,
        /**
         * The {@link SynchronizationModule} is waiting for response from this
         * module.
         */
        WAITING
    }
}
