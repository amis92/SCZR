package burtis.modules.simulation.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.mockups.MockupBus;
import burtis.modules.simulation.exceptions.NoSuchBusStopException;

/**
 * Manages buses in simulation module.
 * 
 * @author Amadeusz Sadowski, Mikołaj Sowiński
 *
 */
public class BusManager
{
    
    /**
     * Logger of the BusManager class.
     */
    private final Logger logger = Logger
            .getLogger(this.getClass().getName());
    
    /**
     * List of buses in the simulation.
     */
    private final Map<Integer,Bus> buses = new HashMap<>();
    
    /**
     * Reference to bus stop manager.
     */
    private final BusStopManager busStopManager;

    /**
     * Generic no-argument constructor.
     */
    public BusManager(BusStopManager busStopManager) {
        this.busStopManager = busStopManager;  
    }

    /**
     * Returns bus of given id.
     * 
     * @param busId
     *            bus id
     * @return Bus
     */
    public Bus getBusById(int busId)
    {
        return buses.get(busId);
    }

    /**
     * Adds new bus of given capacity. New buses are created in a depot state.
     * 
     * @param capacity
     *            bus capacity
     * @return newly created bus
     */
    public Bus add(int capacity)
    {
        Bus newBus = new Bus(capacity, busStopManager);
        buses.put(newBus.getId(), newBus);
        return newBus;
    }

    /**
     * Executes {@link Bus#updatePosition} on every bus which state is running.
     * 
     * @throws NoSuchBusStopException 
     */
    public void updateBusPositions() throws NoSuchBusStopException
    {        
        for (Bus bus : buses.values())
        {
            if (bus.getState() == Bus.State.RUNNING)
            {
                bus.updateBusPosition();
            }
        }
    }

    /**
     * Withdraws bus of given id. Bus will be withdrawn to the depot at the
     * moment of the arrival to the terminus.
     * 
     * @param busId
     *            bus id
     */
    public void withdrawBus(int busId)
    {
        Bus bus = buses.get(busId);
        if (bus != null)
        {
            bus.setGoToDepot(true);
        }
        else
        {
            logger.log(Level.WARNING, "No such bus {0}", busId);
        }
    }

    /**
     * Sets bus to start at next iteration after arriving from the depot. 
     * Clears bus cycles count.
     * 
     * @param busId
     *            id of the bus
     *            
     * @throws NoSuchBusStopException 
     */
    public void sendFromDepot(int busId) throws NoSuchBusStopException
    {
        Bus bus = buses.get(busId);
        if (bus != null)
        {
            bus.sendFromDepot();
        }
        else
        {
            logger.log(Level.WARNING, "No such bus {0}", busId);
        }
    }
   
    /**
     * Sets bus to start at next iteration after being at the terminus.
     * Increments bus cycles count.
     * 
     * @param busId
     *            id of the bus
     */
    public void sendFromTerminus(int busId)
    {
        Bus bus = buses.get(busId);
        if (bus != null)
        {
            bus.sendFromTerminus();
        }
        else
        {
            logger.log(Level.WARNING, "No such bus {0}", busId);
        }
    }
    
    /**
     * Generates list of {@link MockupBus}.
     * 
     * @return - list of {@link MockupBus}
     */
    public List<MockupBus> getMockups()
    {
        List<MockupBus> mockups = new ArrayList<>();
        for (Bus bus : buses.values())
        {
            mockups.add(new MockupBus(bus));
        }
        return mockups;
    }
}
