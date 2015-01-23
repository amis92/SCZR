package burtis.common.events.flow;

import burtis.common.events.AbstractEventHandler;

/**
 * Requests to pause continuous ticking.
 * 
 * @author Mikołaj Sowiński
 */
public class PauseSimulationEvent extends FlowEvent
{
    private static final long serialVersionUID = 1L;

    public PauseSimulationEvent(String sender)
    {
        super(sender);
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
