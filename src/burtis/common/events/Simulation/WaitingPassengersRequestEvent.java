/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events.Simulation;

import burtis.common.events.PassengerInfoRequestEvent;

/**
 *
 * @author Mikołaj Sowiński
 */
public class WaitingPassengersRequestEvent extends PassengerInfoRequestEvent {

    private final int busStopId;
    
    public WaitingPassengersRequestEvent(String sender, int busStopId) {
        super(sender);
        this.busStopId = busStopId;
    }

    public int getBusStopId() {
        return busStopId;
    }
    
    
    
}
