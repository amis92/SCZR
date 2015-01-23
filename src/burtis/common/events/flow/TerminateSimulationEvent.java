package burtis.common.events.flow;

import burtis.common.events.AbstractEventHandler;

/**
 * Informs that simulation is or should be shut down and no other events will or
 * should be sent.
 * 
 * @author Mikołaj Sowiński
 */
public class TerminateSimulationEvent extends FlowEvent
{
    private static final long serialVersionUID = 1L;

    public TerminateSimulationEvent(String sender)
    {
        super(sender, new String[] {});
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
