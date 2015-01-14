package burtis.common.events.flow;

import burtis.common.events.AbstractEventHandler;

/**
 * Event to be sent upon module main loop completion.
 * 
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 */
public class CycleCompletedEvent extends FlowEvent
{
    private static final long serialVersionUID = 1L;
    /**
     * Which iteration it concerns.
     */
    private final long iteration;

    public CycleCompletedEvent(String sender, long iteration)
    {
        super(sender);
        this.iteration = iteration;
    }

    public long iteration()
    {
        return iteration;
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
