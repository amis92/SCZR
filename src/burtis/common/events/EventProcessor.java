package burtis.common.events;

import burtis.common.events.Sync.TickEvent;
import burtis.common.events.gui.DoStepEvent;
import burtis.common.events.gui.PauseSimulationEvent;
import burtis.common.events.gui.StartSimulationEvent;

/**
 * Represents empty processor of all existing events. Each method can be
 * overridden, but by default has empty body. To be inherited.
 * 
 * @author Amadeusz Sadowski
 *
 */
public abstract class EventProcessor
{
    public void process(SimulationEvent event)
    {
        System.out.println("Stack");
    }
    
    public void process(DoStepEvent event)
    {
    }
    
    public void process(PauseSimulationEvent event) 
    {              
    }

    public void process(ChangeSimulationModeEvent event)
    {
    }

    public void process(ChangeSimulationSpeedEvent event)
    {
    }

    public void process(CycleCompletedEvent event)
    {
    }

    public void process(ErrorEvent event)
    {
    }

    public void process(SlowDownEvent event)
    {
    }

    public void process(StartSimulationEvent event)
    {
    }

    public void process(TerminateSimulationEvent event)
    {
    }

    public void process(TickEvent event)
    {
    }
}
