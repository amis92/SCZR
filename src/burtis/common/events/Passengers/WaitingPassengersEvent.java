package burtis.common.events.Passengers;

import java.util.Map;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;

/**
 *
 * @author Mikołaj Sowiński
 */
public class WaitingPassengersEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;
    
    private final Map<Integer,Boolean> busIdWaitingPassengersList;
    
    public WaitingPassengersEvent(String sender, Map<Integer,Boolean> busIdWaitingPassengersList)
    {
        super(sender);
        this.busIdWaitingPassengersList = busIdWaitingPassengersList;
    }

    public Map<Integer, Boolean> getBusIdWaitingPassengersList()
    {
        return busIdWaitingPassengersList;
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
