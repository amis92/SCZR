/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events.Passengers;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.SimulationEvent;

/**
 *
 * @author Mikołaj Sowiński
 */
public class WaitingPassengersEvent extends SimulationEvent {
    
    private final int busStopId;
    private final int waitingPassengers;

    public WaitingPassengersEvent(String sender, int busStopId, int waitingPassengers) {
        super(sender);
        this.busStopId = busStopId;
        this.waitingPassengers = waitingPassengers;
    }

    public int getBusStopId() {
        return busStopId;
    }

    public int getWaitingPassengers() {
        return waitingPassengers;
    }

    public void visit(AbstractEventProcessor eventProcessor)
    {
        eventProcessor.process(this);
    }
}
