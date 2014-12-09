package burtis.common.events.Passengers;

import burtis.common.events.SimulationEvent;
import burtis.modules.network.NetworkConfig;
import burtis.modules.simulation.Simulation;

/**
 *
 * @author Mikołaj Sowiński
 */
public class BusStopsListRequestEvent extends SimulationEvent {

    public BusStopsListRequestEvent(String sender) {
        super(sender);
    }
    
    public BusStopsListRequestEvent() {
        super(
            NetworkConfig.defaultConfig().getModuleConfigs().get(NetworkConfig.PSNGR_MODULE).getModuleName(),
            new String[] { NetworkConfig.defaultConfig().getModuleConfigs().get(NetworkConfig.SIM_MODULE).getModuleName() });
    }
    
}
