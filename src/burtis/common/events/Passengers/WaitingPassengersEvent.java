package burtis.common.events.Passengers;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;

/**
 *
 * @author Mikołaj Sowiński
 */
public class WaitingPassengersEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;
    private final int busStopId;
    private final int waitingPassengers;

    public WaitingPassengersEvent(String sender, int busStopId,
            int waitingPassengers)
    {
        super(sender);
        this.busStopId = busStopId;
        this.waitingPassengers = waitingPassengers;
    }

    public int getBusStopId()
    {
        return busStopId;
    }

    public int getWaitingPassengers()
    {
        return waitingPassengers;
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
