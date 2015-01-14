package burtis.common.events.flow;

import burtis.common.events.AbstractEventHandler;

/**
 * Event to be sent when sender module is ready to start simulation.
 * 
 * @author Mikołaj Sowiński
 */
public class ModuleReadyEvent extends FlowEvent
{
    private static final long serialVersionUID = 1L;
    private final long iteration;

    public ModuleReadyEvent(String sender, long iteration)
    {
        super(sender);
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
