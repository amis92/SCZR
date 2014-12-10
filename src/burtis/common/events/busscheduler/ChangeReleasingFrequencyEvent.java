package burtis.common.events.busscheduler;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.SimulationEvent;

/**
 * Number of time delta between bus releases from depot.
 * 
 * @author Mikołaj
 */
public class ChangeReleasingFrequencyEvent extends SimulationEvent
{
    private static final long serialVersionUID = 446524068178435062L;
    private long newReleasingFrequency;

    public ChangeReleasingFrequencyEvent(String sender, String[] recipients,
            long newReleasingFrequency)
    {
        super(sender, recipients);
    }

    public long getNewReleasingFrequency()
    {
        return newReleasingFrequency;
    }

    @Override
    public void visit(AbstractEventProcessor eventProcessor)
    {
        eventProcessor.process(this);
    }
}
