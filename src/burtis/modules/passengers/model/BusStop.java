package burtis.modules.passengers.model;

import java.util.LinkedList;
import java.util.Queue;

import burtis.modules.passengers.Managers;

/**
 * Representation of bus stop in passengers module.
 * 
 * @author Mikołaj Sowiński
 */
public class BusStop 
{
    
    /**
     * Bus stop name.
     */
    private final String name;
    
    /**
     * Position of the bus stop.
     */
    private final int position;
    
    /**
     * Queue of waiting passengers.
     */
    private final Queue<Passenger> passengerQueue = new LinkedList<>();
    
    /**
     * Queue of waiting buses.
     */
    private final Queue<Bus> busQueue = new LinkedList<>();
    
    /**
     * Reference to the bus being at the bus stop.
     */
    private Bus busAtBusStop;
    
    /**
     * Reference to managers container.
     */
    private final Managers managers;
    
    /**
     * Constructor.
     * 
     * @param id bus stop id
     * @param name bus stop name
     * @param mangers Managers object
     */
    BusStop(String name, int position, Managers mangers) {
        this.name = name;
        this.managers = mangers;
        this.position = position;
    }
    
/* ##############################################
 * GETTERS AND SETTERS
 * ########################################### */
    
    public String getName() {
        return name;
    }

    public int getPosition()
    {
        return position;
    }

    public Queue<Passenger> getPassengerQueue() {
        return passengerQueue;
    }
    
    public Bus getCurrentBus() {
        return busAtBusStop;
    }

/* ##############################################
 * END OF GETTERS AND SETTERS
 * ########################################### */
    
    /**
     * Puts bus to the FIFO queue of buses waiting at the bus stop.
     * 
     * @param bus Bus to be enqueued
     */
    public void enqueueBus(Bus bus) {
        busQueue.add(bus);
        //nextBus();
    }
    
    /**
     * Puts passenger to the FIFO queue of passengers waiting at the bus stop.
     * 
     * @param passenger passenger to be queued
     */
    public void enqueuePassenger(Passenger passenger) {
        passengerQueue.add(passenger);
    }
    
    /**
     * Empties bus stop.
     */
    public void departBus() {
        busAtBusStop = null;
    }
    
    /**
     * If bus stop is empty takes next bus from the queue and creates transaction.
     */
    public void nextBus() {
        if(busAtBusStop == null) {
            busAtBusStop = busQueue.poll();
            if(busAtBusStop != null) {
                managers.getTransactionManager().addTransaction(new Transaction(busAtBusStop, this, managers));
            }
        }
    }
    
    /**
     * Returns next passenger from the queue and removes him from that queue.
     * 
     * @return Passenger
     */
    public Passenger getNextPassenger() {
        return passengerQueue.poll();
    }
    
    /**
     * Returns number of waiting passengers.
     * 
     * @return number of passengers
     */
    public int waitingPassengers() {
        return passengerQueue.size();
    }
    
    @Override
    public String toString() {
        String currnetBusIdString = busAtBusStop == null ? "none" : (new Integer(busAtBusStop.getId())).toString();  
        String busStopString = "Bus stop: " + name + " current bus: " + currnetBusIdString +"\n\tBuses: ";
        
        for(Bus bus : busQueue) {
            busStopString += bus.getId();
        }
        
        return busStopString+"\n";
    }
    
}
