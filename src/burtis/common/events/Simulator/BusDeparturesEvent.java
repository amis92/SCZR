package burtis.common.events.Simulator;

import java.util.List;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;

/**
 * Event transferring list of bus departures in the current iteration.
 * 
 * @author Mikołaj Sowiński
 */
public class BusDeparturesEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;
    
    public class BusDepartureInfo {
        public final int busId; 
        public final int nexBusStopId;
        BusDepartureInfo(int busId, int nextBusStopId) {
            this.busId = busId;
            this.nexBusStopId = nextBusStopId;
        }
    }
    
    private final List<BusDepartureInfo> departuresList; 

    public BusDeparturesEvent(String sender, List<BusDepartureInfo> departuresList) 
    {
        super(sender);
        this.departuresList = departuresList;        
    }

    public List<BusDepartureInfo> getDeparturesList()
    {
        return departuresList;
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
