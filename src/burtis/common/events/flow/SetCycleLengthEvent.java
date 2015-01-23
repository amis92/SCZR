package burtis.common.events.flow;

import burtis.common.events.AbstractEventHandler;

/**
 * Informs synchronization module of the new requested period between ticks.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class SetCycleLengthEvent extends FlowEvent
{
    private static final long serialVersionUID = -7989779856296248255L;
    private final long cycleLength;

    public SetCycleLengthEvent(String sender, long cycleLength)
    {
        super(sender);
        this.cycleLength = cycleLength;
    }

    @Override
    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }

    public long getCycleLength()
    {
        return cycleLength;
    }
}
