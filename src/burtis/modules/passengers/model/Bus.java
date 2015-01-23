package burtis.modules.passengers.model;

import java.util.ArrayList;
import java.util.List;

import burtis.common.constants.SimulationModuleConsts;
import burtis.modules.passengers.Managers;

/**
 * Representation of bus in passengers module.
 * 
 * @author Mikołaj Sowiński
 */
public class Bus
{
    
    /**
     * Bus states.
     */
    private enum State
    {
        /**
         * When at the bus stop.
         */
        ATBUSSTOP, 
        
        /**
         * When in between bus stops.
         */
        RUNNING, 
        
        /**
         * When waiting in the queue at the bus stop.
         */
        WAITING
    }

    /**
     * Current bus state.
     */
    private State state;
    
    /**
     * Bus id.
     */
    private final int id;
    
    /**
     * Reference to the next bus stop requested by passengers.
     */
    private BusStop requestedBusStop;
    
    /**
     * List of passengers traveling with this bus.
     */
    private final List<Passenger> passengers = new ArrayList<>();
    
    /**
     * Reference to managers.
     */
    //private final Managers managers;
    
    /**
     * Constructor.
     * 
     * @param id bus id
     */
    public Bus(int id, Managers managers)
    {
        this.id = id;
        //this.managers = managers;
    }

    
/* ##############################################
 * GETTERS AND SETTERS
 * ########################################### */
    
    public int getId()
    {
        return id;
    }

    public State getState()
    {
        return state;
    }

    public List<Passenger> getPassengers()
    {
        return passengers;
    }

    public BusStop getRequestedBusStop()
    {
        return requestedBusStop;
    }
    
/* ##############################################
 * END OF GETTERS AND SETTERS
 * ########################################### */   
  
    /**
     * Returns number of free places in the bus.
     * @return number of free places
     */
    public int getFreePlaces()
    {
        return SimulationModuleConsts.BUS_CAPACITY - passengers.size();
    }

    /**
     * Changes state of the bus to {@link State#ATBUSSTOP}.
     */
    public void arrive()
    {
        state = State.ATBUSSTOP;
    }

    /**
     * Changes state of the bus to {@link State#WAITING}.
     */
    public void waiting()
    {
        state = State.WAITING;
    }

    /**
     * Order bus to depart from the bus stop.
     * 
     * Changes state of the bus to {@link State#RUNNING}
     * and sets field {@link Bus#requestedBusStop}.
     */
    public void depart()
    {
        requestedBusStop = getNearestRequestedBusStop();
        state = State.RUNNING;
    }
    
    /**
     * Gets nearest bus stop requested by the passengers.
     * 
     * @return nearest requested {@link BusStop}
     */
    private BusStop getNearestRequestedBusStop() {
        
        BusStop nearestRequestedBusStop = null;
        
        if(passengers.size() > 0) {
            nearestRequestedBusStop = passengers.get(0).getDestination();
        }
                
        if(passengers.size() > 1) {
            for(int i=1; i<passengers.size(); i++) {
                if(passengers.get(i).getDestination().getPosition() < nearestRequestedBusStop.getPosition()) {
                    nearestRequestedBusStop = passengers.get(i).getDestination();
                }
            }
        }
        
        return nearestRequestedBusStop;
        
    }

    /**
     * Removes all passengers from the bus (killing them!) and puts bus into {@link State#RUNNING}.
     */
    void resetBus()
    {
        passengers.clear();
        state = State.RUNNING;
    }
    
    @Override
    public String toString() {
        String passengersString = "";
        for(Passenger passenger : passengers) {
            passengersString += passenger.getId() + " ";
        }
        return "Bus: " + id + " passengers: " + passengersString + "\n";
    }
    

}
