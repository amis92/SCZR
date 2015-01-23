package burtis.common.events.flow;

import burtis.common.events.AbstractEventHandler;

/**
 * Informs that ticking should start continuously.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class StartSimulationEvent extends FlowEvent
{
    private static final long serialVersionUID = 1L;

    public StartSimulationEvent(String sender)
    {
        super(sender);
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
