package burtis.modules.passengers.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import burtis.modules.passengers.Managers;
import burtis.modules.passengers.PassengerModule;
import burtis.modules.passengers.exceptions.NoSuchBusException;

/**
 * 
 * 
 * @author Mikołaj Sowiński
 *
 */
public class BusManager
{

    /**
     * List of available buses.
     */
    private final List<Bus> buses = new ArrayList<>();
    
    /**
     * List of buses departing at the current iteration.
     */
    private final List<Bus> departingBuses = new ArrayList<>();
    
    /**
     * Reference to the managers.
     */
    private final Managers managers;
    
    /**
     * Constructor.
     */
    BusManager(Managers managers) {
        
        this.managers = managers;
        managers.setBusManager(this);
        
    }
    
/* ##############################################
 * GETTERS AND SETTERS
 * ########################################### */
    
    
    
/* ##############################################
 * END OF GETTERS AND SETTERS
 * ########################################### */

    /**
     * Add bus of specified id to the list of known buses.
     * 
     * @param busId
     * @return created Bus object
     */
    public Bus add(int busId)
    {
        Bus bus = new Bus(busId, managers);
        buses.add(bus);
        return bus;
    }
    
    /**
     * Adds bus to the list of departing buses.
     * 
     * @param bus
     */
    public void addToDepartingList(Bus bus) 
    {
        departingBuses.add(bus);
    }
    
    /**
     * Returns bus of given id.
     * 
     * @param id bus id
     * @return bus of given id
     * 
     * @throws NoSuchBusException 
     */
    public Bus getBusById(int id) throws NoSuchBusException {
        for (Bus bus : buses)
        {
            if (bus.getId() == id)
                return bus;
        }
        throw new NoSuchBusException(id);
    }
    
}
