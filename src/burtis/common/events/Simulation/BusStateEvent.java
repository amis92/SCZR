package burtis.common.events.Simulation;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.SimulationEvent;
import burtis.common.mockups.MockupBusState;
import java.util.List;

/**
 *
 * @author Mikołaj
 */
public class BusStateEvent extends SimulationEvent {
    
    public static class BusInfo {
        public final int busId;
        public final MockupBusState state;
        public final int lengthPassed;

        public BusInfo(int busId, MockupBusState state, int lengthPassed) {
            this.busId = busId;
            this.state = state;
            this.lengthPassed = lengthPassed;
        }
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
