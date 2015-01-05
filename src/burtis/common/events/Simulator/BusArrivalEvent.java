package burtis.common.events.Simulator;

import java.util.Map;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;

/**
 * Event transferring list of buses that arrives at the bus stops
 * in the current iteration.
 * 
 * @author Mikołaj Sowiński
 */
public class BusArrivalEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;
    
    /**
     * List of buses IDs.
     */
    private final Map<Integer,Integer> busArrivalList;

    public BusArrivalEvent(String sender, Map<Integer,Integer> busArrivalList)
    {
        super(sender);
        this.busArrivalList = busArrivalList;
    }

    public Map<Integer,Integer> getBusArrivalList()
    {
        return busArrivalList;
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
