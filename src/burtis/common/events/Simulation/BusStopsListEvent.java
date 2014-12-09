/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events.Simulation;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.SimulationEvent;
import burtis.modules.network.NetworkConfig;

import java.io.Serializable;
import java.util.List;

/**
 * Response for BusStopsListRequestEvent.
 * 
 * @author Mikołaj Sowiński
 */
public class BusStopsListEvent extends SimulationEvent {

    public static class BusStop implements Serializable {
        
        public final int busStopId;
        public final String busStopName;

        public BusStop(int busStopId, String busStopName) {
            this.busStopId = busStopId;
            this.busStopName = busStopName;
        }
        
    }
    
    private final List<BusStop> busStops;
    
    public BusStopsListEvent(String sender, List<BusStop> busStops) {
        super(sender);
        this.busStops = busStops;
    }
    
    public BusStopsListEvent(List<BusStop> busStops) {
        super(NetworkConfig.defaultConfig().getModuleConfigs().get(NetworkConfig.SIM_MODULE).getModuleName());
        this.busStops = busStops;
    }

    public List<BusStop> getBusStops() {
        return busStops;
    }

    public void visit(AbstractEventProcessor eventProcessor)
    {
        eventProcessor.process(this);
    }
}
