package burtis.common.events.flow;

import burtis.common.events.AbstractEventHandler;

/**
 * @author Mikołaj Sowiński
 *
 */
public class TickEvent extends FlowEvent
{
    private final long iteration;

    public TickEvent(String sender, long iteration)
    {
        super(sender, new String[] {});
        this.iteration = iteration;
    }

    public long iteration()
    {
        return iteration;
    }

    private static final long serialVersionUID = 1L;

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
