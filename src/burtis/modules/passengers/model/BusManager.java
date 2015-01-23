package burtis.modules.passengers.model;

import java.util.ArrayList;
import java.util.List;

import burtis.common.events.Simulator.BusDepartureInfo;
import burtis.modules.passengers.Managers;
import burtis.modules.passengers.exceptions.NoSuchBusException;

/**
 * Manages buses existing on the simulation on passenger side.
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
    private final Managers managers;

    public BusManager(Managers managers)
    {
        this.managers = managers;
        managers.setBusManager(this);
    }

    /**
     * Add bus of specified id to the list of known buses. If bus of given id
     * exists it is not recreated.
     * 
     * @param busId
     *            - id of the bus to be added or created.
     * @return created Bus object or existing bus of given id.
     */
    public Bus add(int busId)
    {
        Bus bus;
        try
        {
            bus = getBusById(busId);
            return bus;
        }
        catch (NoSuchBusException ex)
        {
            bus = new Bus(busId, managers);
            buses.add(bus);
            return bus;
        }
    }

    /**
     * Adds bus to the list of departing buses.
     * 
     * @param bus
     *            - the bus to be added.
     */
    public void addToDepartingList(Bus bus)
    {
        departingBuses.add(bus);
    }

    /**
     * Searches for a bus with provided id.
     * 
     * @param id
     *            - the identifier of the bus to be found.
     * @return found bus.
     * 
     * @throws NoSuchBusException
     *             - if such a bus wasn't found.
     */
    public Bus getBusById(int id) throws NoSuchBusException
    {
        for (Bus bus : buses)
        {
            if (bus.getId() == id)
                return bus;
        }
        throw new NoSuchBusException(id);
    }

    /**
     * List is cleared upon retrieval.
     * 
     * @return departure information for each departing bus.
     */
    public List<BusDepartureInfo> getBusDepartureInfoList()
    {
        List<BusDepartureInfo> list = new ArrayList<>();
        for (Bus bus : departingBuses)
        {
            if (bus.getRequestedBusStop() != null)
            {
                list.add(new BusDepartureInfo(bus.getId(), bus
                        .getRequestedBusStop().getName()));
            }
            else
            {
                list.add(new BusDepartureInfo(bus.getId(), null));
            }
        }
        departingBuses.clear();
        return list;
    }

    @Override
    public String toString()
    {
        String out = "";
        for (Bus bus : buses)
        {
            out += bus.toString();
        }
        return out;
    }
}
