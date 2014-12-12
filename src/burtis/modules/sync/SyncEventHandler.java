package burtis.modules.sync;

import java.util.logging.Level;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.CycleCompletedEvent;
import burtis.common.events.SimulationEvent;
import burtis.common.events.TerminateSimulationEvent;
import burtis.common.events.gui.DoStepEvent;
import burtis.common.events.gui.PauseSimulationEvent;
import burtis.common.events.gui.StartSimulationEvent;

import com.sun.istack.internal.logging.Logger;

/**
 * Handles incoming events by calling appropriate {@link SynchronizationModule}
 * methods.
 *
 * @author Mikołaj Sowiński
 */
public class SyncEventHandler extends AbstractEventProcessor
{
    private final static Logger logger = Logger
            .getLogger(SynchronizationModule.class);
    private final SynchronizationModule syncModule;
    private final WatchdogService watchdogService;

    public SyncEventHandler(SynchronizationModule syncModule,
            WatchdogService watchdogService)
    {
        this.syncModule = syncModule;
        this.watchdogService = watchdogService;
    }

    @Override
    public void defaultHandle(SimulationEvent event)
    {
        logger.log(Level.WARNING, "Unhandled event {0}", event.getClass()
                .getSimpleName());
    }

    @Override
    public void process(TerminateSimulationEvent event)
    {
        syncModule.shutdown();
    }

    @Override
    public void process(StartSimulationEvent event)
    {
        syncModule.startSimulation();
    }

    @Override
    public void process(PauseSimulationEvent event)
    {
        syncModule.pauseSimulation();
    }

    @Override
    public void process(DoStepEvent event)
    {
        syncModule.doStep();
    }

    @Override
    public void process(CycleCompletedEvent event)
    {
        watchdogService.handleModuleResponded(event.sender(),
                event.iteration() == syncModule.getIteration());
    }
}
