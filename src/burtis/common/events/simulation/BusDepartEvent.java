package burtis.common.events.simulation;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;

/**
 * Event sent when bus departs from the bus stop.
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
