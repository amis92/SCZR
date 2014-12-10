package burtis.common.events.Simulation;

import java.util.List;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.SimulationEvent;
import burtis.modules.simulation.models.Bus;

/**
 *
 * @author Mikołaj
 */
public class BusStateEvent extends SimulationEvent
{
    private static final long serialVersionUID = 7441022056679823117L;

    public static class BusInfo
    {
        public final int busId;
        public final Bus.State state;
        public final int lengthPassed;

        public BusInfo(int busId, Bus.State state, int lengthPassed)
        {
            this.busId = busId;
            this.state = state;
            this.lengthPassed = lengthPassed;
        }
    }

    private final List<BusInfo> buses;

    public BusStateEvent(String sender, List<BusInfo> buses)
    {
        super(sender);
        this.buses = buses;
    }

    @Override
    public void visit(AbstractEventProcessor eventProcessor)
    {
        eventProcessor.process(this);
    }

    public List<BusInfo> getBuses()
    {
        return buses;
    }
}
