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
public class PassengerTransaction {
    
    private final int busStopId;
    private final int busId;
    private final int places;
    
    public PassengerTransaction(int busStopId, int busId, int places) {
        this.places = places;
        this.busStopId = busStopId;
        this.busId = busId;
    }

    public int getPlaces() {
        return places;
    }
    
    public int getBusStopId() {
        return busStopId;
    }

    public int getBusId() {
        return busId;
    }
    
    
    
    
}
