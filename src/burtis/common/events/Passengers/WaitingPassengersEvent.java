package burtis.common.events.Passengers;

import java.util.Map;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;
import burtis.modules.network.NetworkConfig;

/**
 * Response of the passengers module for the {@link WaitingPassengersRequestEvent}
 * containing map with pair: <bus stop id,  true if anyone is waiting / false if 
 * nobody is waiting at the corresponding bus stop>.
 *
 * @author Mikołaj Sowiński
 */
public class WaitingPassengersEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;
    
    private final Map<String,Boolean> busIdWaitingPassengersList;
    
    public WaitingPassengersEvent(String sender, Map<String,Boolean> busIdWaitingPassengersList)
    {
        super(sender, new String[] {NetworkConfig.getModuleName(NetworkConfig.SIM_MODULE)});
        this.busIdWaitingPassengersList = busIdWaitingPassengersList;
    }

    public Map<String, Boolean> getBusIdWaitingPassengersList()
    {
        return busIdWaitingPassengersList;
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
