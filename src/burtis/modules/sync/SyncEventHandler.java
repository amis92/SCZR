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
 *
 * @author Mikołaj
 */
public class SyncEventHandler extends AbstractEventProcessor
{
    private final static Logger logger = Logger
            .getLogger(SynchronizationModule.class);
    private final SynchronizationModule syncModule;

    public SyncEventHandler(SynchronizationModule syncModule)
    {
        this.syncModule = syncModule;
    }

    @Override
    public void defaultHandle(SimulationEvent event)
    {
        logger.log(Level.WARNING, "Unknown event {0}", event.getClass()
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
        // Being not in sync is unacceptable.
        if (event.iteration() != syncModule.getIteration())
        {
            logger.severe("Error: Critical module '{0}' is out of sync.",
                    new Object[] { event.sender() });
            logger.severe("Shutting down sync module.");
            syncModule.shutdown();
        }
        else
        {
            Module.setReady(event.sender());
        }
    }
}
