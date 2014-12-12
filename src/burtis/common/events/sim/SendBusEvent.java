package burtis.common.events.sim;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;

/**
 *
 * @author Mikołaj Sowiński
 */
public class SendBusEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;
    private final int busId;

    public SendBusEvent(String sender, String[] recipients, int busId)
    {
        super(sender, recipients);
        this.busId = busId;
    }

    public int busId()
    {
        return busId;
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
