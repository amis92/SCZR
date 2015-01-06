package burtis.common.events.Passengers;

import java.util.List;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;
import burtis.modules.network.NetworkConfig;
import burtis.modules.simulation.Simulation;

/**
 * Event send by {@link Simulation} to get info on passengers waiting at the bus stops.
 *
 * @author Mikołaj Sowiński
 */
public class WaitingPassengersRequestEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;
    private final List<Integer> busStopIdsList;

    public WaitingPassengersRequestEvent(String sender, List<Integer> busStopIdsList)
    {
        super(sender, new String[] {NetworkConfig.getModuleName(NetworkConfig.PSNGR_MODULE)});
        this.busStopIdsList = busStopIdsList;
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }

    public List<Integer> getBusStopIdsList()
    {
        return busStopIdsList;
    }
}
