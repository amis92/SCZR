package burtis.common.events.Simulator;

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
    private final long newReleasingFrequency;

    public ChangeReleasingFrequencyEvent(String sender, String[] recipients,
            long newReleasingFrequency)
    {
        super(sender, recipients);
        this.newReleasingFrequency = newReleasingFrequency;
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
