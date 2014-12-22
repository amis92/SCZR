package burtis.modules.simulation.models;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Depot representation.
 * 
 * It is essentially a store for buses.
 * 
 * @author Mikołaj Sowiński
 *
 */
public class Depot
{

    /**
     * Queue of buses in the depot.
     * 
     * Using queue makes buses exists in a FIFO order.
     */
    private static final Queue<Bus> buses = new LinkedList<>();
    
    /**
     * Adds bus to the buses registered in the depot.
     * 
     * @param bus bus to be added
     */
    public void putBus(Bus bus) {
        buses.add(bus);
    }
    
    /**
     * Takes bus of given id from the depot.
     * 
     * Null is returned if bus is not registered in the depot.
     * 
     * @param busId id of requested bus
     * @return Bus requested bus or null
     */
    public Bus getBus(int busId) {
        for(Bus bus : buses) {
            if(bus.getId()==busId) {
                buses.remove(bus);
                return bus;
            }
        }
        return null;
    }
    
    /**
     * Returns first bus from the depot's FIFO queue.
     * 
     * @return first bus from the queue
     */
    public Bus getBus() {
        return buses.poll();
    }
  
}
