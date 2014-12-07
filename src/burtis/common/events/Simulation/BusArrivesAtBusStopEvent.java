/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events.Simulation;

import burtis.common.events.SimulationEvent;

/**
 *
 * @author Mikołaj Sowiński
 */
public class BusArrivesAtBusStopEvent extends SimulationEvent {

    private final int busId;
    private final int busStopId;
    
    public BusArrivesAtBusStopEvent(String sender, int busId, int busStopId) {
        super(sender);
        this.busId = busId;
        this.busStopId = busStopId;
    }

    public int getBusId() {
        return busId;
    }

    public int getBusStopId() {
        return busStopId;
    }
    
    
    
}
