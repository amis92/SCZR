/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events.Simulation;

import burtis.common.events.SimulationEvent;
import burtis.modules.passengers.BusStop;
import java.util.List;

/**
 * Response for BusStopsListRequestEvent.
 * 
 * @author Mikołaj Sowiński
 */
public class BusStopsListEvent extends SimulationEvent {

    private final List<BusStop> busStops;
    
    public BusStopsListEvent(String sender, List<BusStop> busStops) {
        super(sender);
        this.busStops = busStops;
    }

    public List<BusStop> getBusStops() {
        return busStops;
    }
    
    
    
}
