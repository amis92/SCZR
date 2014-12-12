package burtis.common.events.passengers;

import burtis.common.events.AbstractEventHandler;

/**
 *
 * @author Mikołaj Sowiński
 */
public class WaitingPassengersRequestEvent extends PassengerInfoRequestEvent
{
    private static final long serialVersionUID = 1L;
    private final int busStopId;

    public WaitingPassengersRequestEvent(String sender, int busStopId)
    {
        super(sender);
        this.busStopId = busStopId;
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
