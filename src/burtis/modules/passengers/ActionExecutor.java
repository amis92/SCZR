package burtis.modules.passengers;

import java.util.List;
import java.util.Map;

import burtis.common.events.EventSender;
import burtis.common.events.MainMockupEvent;
import burtis.common.events.Passengers.WaitingPassengersEvent;
import burtis.common.events.Simulator.BusDepartureInfo;
import burtis.common.events.Simulator.BusDeparturesEvent;
import burtis.common.mockups.Mockup;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.client.ClientModule;

public class ActionExecutor extends EventSender
{
    
    public ActionExecutor(ClientModule clientModule, NetworkConfig netConfig)
    {
        super(clientModule, netConfig);
    }
    
    public void sendWaitingPassengersRequestResponse(Map<String, Boolean> response) {
        clientModule.send(new WaitingPassengersEvent(sender, response));
    }
    
    public void sendDeparturesList(List<BusDepartureInfo> departureInfoList) {
        clientModule.send(new BusDeparturesEvent(sender, departureInfoList));
    }
    
    public void sendMockups(Mockup mockup) {
        clientModule.send(new MainMockupEvent(sender, mockup));
    }

}
