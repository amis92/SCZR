package burtis.common.events.Passengers;

import burtis.common.events.SimulationEvent;

/**
 *
 * @author Mikołaj Sowiński
 */
public class BusStopsListRequestEvent extends SimulationEvent {

    public BusStopsListRequestEvent(String sender) {
        super(sender);
    }
    
}
