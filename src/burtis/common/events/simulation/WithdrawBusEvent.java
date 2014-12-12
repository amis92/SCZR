package burtis.common.events.simulation;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;

/**
 *
 * @author Mikołaj Sowiński
 */
public class WithdrawBusEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;
    private int busId;

    /**
     * Get the value of busId
     *
     * @return the value of busId
     */
    public int busId()
    {
        return busId;
    }

    public WithdrawBusEvent(String sender, String[] recipients, int busId)
    {
        super(sender, recipients);
        this.busId = busId;
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
