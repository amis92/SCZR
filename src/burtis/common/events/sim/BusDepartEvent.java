package burtis.common.events.sim;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;

/**
 *
 * @author Mikołaj Sowiński
 */
public class BusDepartEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;
    private final int busId;
    private final int nextBusStopId;

    public BusDepartEvent(String sender, int busId, int nextBusStopId)
    {
        super(sender);
        this.busId = busId;
        this.nextBusStopId = nextBusStopId;
    }

    public int getBusId()
    {
        return busId;
    }

    public int getNextBusStopId()
    {
        return nextBusStopId;
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
