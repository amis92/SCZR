package burtis.common.events.flow;

import burtis.common.events.AbstractEventHandler;

/**
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
