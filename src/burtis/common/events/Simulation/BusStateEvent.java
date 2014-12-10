package burtis.common.events.Simulation;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.SimulationEvent;

/**
 *
 * @author Mikołaj
 */
public class BusStateEvent extends SimulationEvent {
    
    public static BussInfo {
        
    }
    
    private final List<BusInfo> 

    public BusStateEvent(String sender) {
        super(sender);
    }

    @Override
    public void visit(AbstractEventProcessor eventProcessor) {
        eventProcessor.process(this);
    }
    
}
