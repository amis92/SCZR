﻿package burtis.modules.passengers.model;

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
     * Bus stop id.
     */
    private final int id;
    
    /**
     * Bus stop name.
     */
    private final String name;
    
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
    BusStop(int id, String name, Managers mangers) {
        this.id = id;
        this.name = name;
        this.managers = mangers;
    }
    
/* ##############################################
 * GETTERS AND SETTERS
 * ########################################### */
        
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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
     * After adding {@link BusStop#nextBus() } is called.
     * 
     * @param bus Bus to be enqueued
     */
    public void enqueueBus(Bus bus) {
        busQueue.add(bus);
        nextBus();
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
     * Empties bus stop and calls {@link BusStop#nextBus() }.
     */
    public void departBus() {
        busAtBusStop = null;
        nextBus();
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
    
}
