/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events;

/**
 *
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 */
public class GetPassengersOnBusStopEvent extends SimulationEvent {

    private final int busStopId;

    /**
     * Get the value of busStopId
     *
     * @return the value of busStopId
     */
    public int getBusStopId() {
        return busStopId;
    }
   
    public GetPassengersOnBusStopEvent(String sender, String[] recipients, int busStopId) {
        super(sender, recipients);
        this.busStopId = busStopId;
    }
    
}
