package burtis.common.events.Simulator;

import java.util.List;

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
    private final List<Integer> busArrivalList;

    public BusArrivalEvent(String sender, List<Integer> busArrivalList)
    {
        super(sender);
        this.busArrivalList = busArrivalList;
    }

    public List<Integer> getBusArrivalList()
    {
        return busArrivalList;
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
