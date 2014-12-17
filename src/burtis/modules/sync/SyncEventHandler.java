package burtis.modules.sync;

import java.util.logging.Level;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;
import burtis.common.events.flow.CycleCompletedEvent;
import burtis.common.events.flow.DoStepEvent;
import burtis.common.events.flow.PauseSimulationEvent;
import burtis.common.events.flow.StartSimulationEvent;
import burtis.common.events.flow.TerminateSimulationEvent;

import com.sun.istack.internal.logging.Logger;

/**
 * Handles incoming events by calling appropriate {@link SynchronizationModule}
 * 's or {@link WatchdogService}'s methods.
 *
 * @author Mikołaj Sowiński
 */
class SyncEventHandler extends AbstractEventHandler
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
