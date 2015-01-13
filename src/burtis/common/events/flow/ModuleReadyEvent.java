package burtis.common.events.flow;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;

/**
 * Event to be sent when sender module is ready to start simulation.
 * 
 * @author Mikołaj Sowiński
 */
public class ModuleReadyEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;
    
    private final long iteration;

    public ModuleReadyEvent(String sender, long iteration)
    {
        super(sender);
        this.iteration = iteration;
    }

    public ModuleReadyEvent(String sender, String[] recipients, long iteration)
    {
        super(sender, recipients);
        this.iteration = iteration;
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }

    public long getIteration()
    {
        return iteration;
    }
    
}
