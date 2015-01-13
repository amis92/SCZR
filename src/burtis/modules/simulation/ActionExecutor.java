package burtis.modules.simulation;

import java.util.List;
import java.util.Map;

import burtis.common.events.EventSender;
import burtis.common.events.Passengers.WaitingPassengersRequestEvent;
import burtis.common.events.Simulator.BusArrivalEvent;
import burtis.common.events.Simulator.BusMockupsEvent;
import burtis.common.events.Simulator.BusStopsListEvent;
import burtis.common.mockups.MockupBus;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.client.ClientModule;
import burtis.modules.simulation.models.BusStop;

/**
 * Simulation action executor.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class ActionExecutor extends EventSender
{

    public ActionExecutor(ClientModule clientModule, NetworkConfig netConfig)
    {
        super(clientModule, netConfig);
    }
    
    public void sendBusArrivalEvent(Map<Integer,Integer> busArrivalList) {
        clientModule.send(new BusArrivalEvent(sender, busArrivalList));
    }

    public void sendBusMockupEvent(long iteration, List<MockupBus> busMockups)
    {
        clientModule.send(new BusMockupsEvent(sender,
                busMockups, iteration));
    }
    
    public void sendWaitingPassengersQueryRequest(List<Integer> busStopsIdsList) {
        clientModule.send(new WaitingPassengersRequestEvent(sender, busStopsIdsList));
    }

}
