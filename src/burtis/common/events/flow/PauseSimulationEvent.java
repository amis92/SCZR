package burtis.common.events.flow;

import burtis.common.events.AbstractEventHandler;

/**
 *
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
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
