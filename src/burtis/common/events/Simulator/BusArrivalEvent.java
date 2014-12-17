package burtis.common.events.Simulator;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;

/**
 * Event sent when bus arrives at the bus stop.
 * 
 * @author Mikołaj Sowiński
 */
public class BusArrivalEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;
    private final int busId;
    private final int busStopId;

    public BusArrivalEvent(String sender, int busId, int busStopId)
    {
        super(sender);
        this.busId = busId;
        this.busStopId = busStopId;
    }

    public int getBusId()
    {
        return busId;
    }

    public int getBusStopId()
    {
        return busStopId;
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
