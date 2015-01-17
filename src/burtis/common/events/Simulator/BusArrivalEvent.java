package burtis.common.events.Simulator;

import java.util.Map;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;
import burtis.modules.network.NetworkConfig;

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
    private final Map<Integer,String> busArrivalList;

    public BusArrivalEvent(String sender, Map<Integer,String> busArrivalList)
    {
        super(sender, new String[] {NetworkConfig.getModuleName(NetworkConfig.PSNGR_MODULE)});
        this.busArrivalList = busArrivalList;
    }

    public Map<Integer,String> getBusArrivalList()
    {
        return busArrivalList;
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
