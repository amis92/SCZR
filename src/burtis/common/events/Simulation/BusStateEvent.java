package burtis.common.events.Simulation;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.SimulationEvent;
import java.util.List;

/**
 *
 * @author Mikołaj
 */
public class BusStateEvent extends SimulationEvent {
    
    public static class BusInfo {
        
    }
    
    private final List<BusInfo> buses; 

    public BusStateEvent(String sender, List<BusInfo> buses) {
        super(sender);
        this.buses = buses;
    }

    @Override
    public void visit(AbstractEventProcessor eventProcessor) {
        eventProcessor.process(this);
    }
    
}
