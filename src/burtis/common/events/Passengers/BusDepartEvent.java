/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events.Passengers;

import burtis.common.events.SimulationEvent;

/**
 *
 * @author Mikołaj Sowiński
 */
public class BusDepartEvent extends SimulationEvent {

    private final int busId;
    private final int nextBusStopId;
    
    public BusDepartEvent(String sender, int busId, int nextBusStopId) {
        super(sender);
        this.busId = busId;
        this.nextBusStopId = nextBusStopId;
    }

    public int getBusId() {
        return busId;
    }

    public int getNextBusStopId() {
        return nextBusStopId;
    }
        
}
