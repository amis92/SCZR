package burtis.common.events.flow;

import burtis.common.events.AbstractEventHandler;

/**
 * Requests another tick to be done in stepping mode, resulting in stepping mode
 * being activated if not yet active.
 *
 * @author Mikołaj Sowiński
 */
public class DoStepEvent extends FlowEvent
{
    private static final long serialVersionUID = 1L;

    public DoStepEvent(String sender)
    {
        super(sender);
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
