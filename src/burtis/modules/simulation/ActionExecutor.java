package burtis.modules.simulation;

import java.util.List;
import java.util.Map;

import burtis.common.events.EventSender;
import burtis.common.events.Passengers.WaitingPassengersRequestEvent;
import burtis.common.events.Simulator.BusArrivalEvent;
import burtis.common.events.Simulator.BusMockupsEvent;
import burtis.common.mockups.MockupBus;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.client.ClientModule;

/**
 * Outgoing network communication management for Simulation.
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

    public void sendBusArrivalEvent(Map<Integer, String> busArrivalList)
    {
        clientModule.send(new BusArrivalEvent(sender, busArrivalList));
    }

    public void sendBusMockupEvent(long iteration, List<MockupBus> busMockups)
    {
        clientModule.send(new BusMockupsEvent(sender, busMockups, iteration));
    }

    public void sendWaitingPassengersQueryRequest(List<Integer> busStopsIdsList)
    {
        clientModule.send(new WaitingPassengersRequestEvent(sender,
                busStopsIdsList));
    }
}
