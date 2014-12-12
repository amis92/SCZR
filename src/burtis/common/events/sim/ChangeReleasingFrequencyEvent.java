package burtis.common.events.sim;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;

/**
 * Number of time delta between bus releases from depot.
 * 
 * @author Mikołaj
 */
public class ChangeReleasingFrequencyEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;
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
    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
