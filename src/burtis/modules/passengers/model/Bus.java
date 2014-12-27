package burtis.modules.passengers.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import burtis.common.constants.SimulationModuleConsts;
import burtis.modules.passengers.EventBuilder;
import burtis.modules.passengers.Managers;
import burtis.modules.passengers.PassengerModule;

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
    private final Managers managers;
    
    /**
     * Constructor.
     * 
     * @param id bus id
     */
    public Bus(int id, Managers managers)
    {
        this.id = id;
        this.managers = managers;
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

    public BusStop getNextBusStop()
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
     * Changes state of the bus to {@link State#RUNNING} and adds bus to the
     * list of buses departing at current iteration ({@link BusManager#addToDepartingList(Bus)}).
     */
    public void depart()
    {
        managers.getBusManager().addToDepartingList(this);
        state = State.RUNNING;
    }

    /**
     * Removes all passengers from the bus (killing them!) and puts bus into {@link State#RUNNING}.
     */
    void resetBus()
    {
        passengers.clear();
        state = State.RUNNING;
    }

}
