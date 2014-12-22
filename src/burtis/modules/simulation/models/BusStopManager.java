package burtis.modules.simulation.models;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import burtis.modules.simulation.exceptions.NoSuchBusStopException;

/**
 * Manages bus stops in simulation module.
 * 
 * @author Mikołaj Sowiński
 *
 */
public class BusStopManager
{
    
    /**
     * Logger of the BusManager class.
     */
    private final Logger logger = Logger
            .getLogger(this.getClass().getName());
    
    /**
     * List of buses in the simulation.
     */
    private final List<BusStop> busStops = new LinkedList<>();

    /**
     * Generic no-argument constructor.
     */
    public BusStopManager() {}
    
    /**
     * Returns first bus stop in the line.
     * 
     * @return first bus stop
     */
    public BusStop getFirstBusStop() {
        return busStops.get(0);
    }
    
    /**
     * Returns bus stop next to the one given as argument.
     * 
     * If terminus is given as an argument terminus is returned.
     *  
     * @throws NoSuchBusStopException if bus stop given as argument does not exist
     * 
     * @returns next bus stop
     */
    public BusStop getNextBusStop(BusStop currentBusStop) throws NoSuchBusStopException {
        
        if(currentBusStop instanceof Terminus) {
            return currentBusStop;
        }
        
        int currentIndex = busStops.indexOf(currentBusStop);
        
        if(currentIndex >= 0) {
            return busStops.get(currentIndex+1);
        }
        else {
            throw new NoSuchBusStopException(currentBusStop);
        }
    }
    
    /**
     * Checks if given bus stop exists.
     * 
     * @throws NoSuchBusStopException if bus stop does not exist
     * 
     * @return bus stop
     */
    public BusStop busStopExists(BusStop busStop) throws NoSuchBusStopException {
        if(busStops.indexOf(busStop) < 0) {
            throw new NoSuchBusStopException(busStop);
        }
        else {
            return busStop;
        }
    }
    
}
