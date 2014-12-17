package burtis.modules.simulation.models;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.mockups.MockupBus;

public class BusManager
{
    private static final Logger logger = Logger.getLogger(BusManager.class
            .getName());
    private final List<Bus> buses = new ArrayList<>();

    public BusManager()
    {
        // TODO Auto-generated constructor stub
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
        for (Bus bus : buses)
        {
            if (bus.getId() == busId)
                return bus;
        }
        return null;
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
        Bus newBus = new Bus(capacity);
        buses.add(newBus);
        return newBus;
    }

    /**
     * Updates bus positions according to its state.
     */
    public void updatePositions()
    {
        for (Bus bus : buses)
        {
            // Running case
            if (bus.getState() == Bus.State.RUNNING)
            {
                bus.recalculatePosition();
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
        Bus bus = getBusById(busId);
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
     * Sets bus to start at next iteration after arriving from the depot. It
     * clears bus cycles count.
     * 
     * @param busId
     *            id of the bus
     */
    public void sendFromDepot(int busId)
    {
        Bus bus = getBusById(busId);
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
     * Generates list of {@link MockupBus}.
     * 
     * @return - list of {@link MockupBus}
     */
    public List<MockupBus> getMockups()
    {
        List<MockupBus> mockups = new ArrayList<>();
        for (Bus bus : buses)
        {
            mockups.add(new MockupBus(bus.getId()));
        }
        return mockups;
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
        Bus bus = getBusById(busId);
        if (bus != null)
        {
            bus.sendFromTerminus();
        }
        else
        {
            logger.log(Level.WARNING, "No such bus {0}", busId);
        }
    }
}
