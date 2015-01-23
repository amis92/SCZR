package burtis.modules.simulation.models;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
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
    // private final Logger logger;
    /**
     * List of buses in the simulation.
     */
    private final List<BusStop> busStops = new LinkedList<>();

    /**
     * Constructor.
     * 
     * Creates bus stops using data from supplied list. The last of supplied bus
     * stops will be of terminus class.
     * 
     * @param busStopsList
     *            - list of entries describing bus stops.
     * @param depot
     *            - reference to the depot object.
     * @param logger
     *            - the logger to be used.
     */
    public BusStopManager(List<Entry<Integer, String>> busStopsList,
            Depot depot, Logger logger)
    {
        for (int i = 0; i < busStopsList.size(); i++)
        {
            Entry<Integer, String> busStopData = busStopsList.get(i);
            if (i != (busStopsList.size() - 1))
            {
                busStops.add(new BusStop(busStopData.getKey(), busStopData
                        .getValue()));
            }
            else
            {
                busStops.add(new Terminus(busStopData.getKey(), busStopData
                        .getValue(), depot));
            }
        }
        // this.logger = logger;
    }

    /**
     * Returns first bus stop in the line.
     * 
     * @return first bus stop
     */
    public BusStop getFirstBusStop()
    {
        return busStops.get(0);
    }

    /**
     * Searches for bus stop next to the one given as argument.
     * 
     * If terminus is given as an argument terminus is returned.
     * 
     * @param currentBusStop
     *            - the bus stop to start search from, and find next to it.
     * 
     * @throws NoSuchBusStopException
     *             if bus stop given as argument does not exist
     * 
     * @return next bus stop on the route.
     */
    public BusStop getNextBusStop(BusStop currentBusStop)
            throws NoSuchBusStopException
    {
        if (currentBusStop instanceof Terminus)
        {
            return currentBusStop;
        }
        int currentIndex = busStops.indexOf(currentBusStop);
        if (currentIndex >= 0)
        {
            return busStops.get(currentIndex + 1);
        }
        else
        {
            throw new NoSuchBusStopException(currentBusStop.toString());
        }
    }

    /**
     * Checks if given bus stop exists.
     * 
     * @param busStop
     *            - the bus stop to be checked.
     * 
     * @throws NoSuchBusStopException
     *             if bus stop does not exist
     * 
     * @return the bus stop given as argument, for chaining methods.
     */
    public BusStop busStopExists(BusStop busStop) throws NoSuchBusStopException
    {
        if (busStops.indexOf(busStop) < 0)
        {
            throw new NoSuchBusStopException(busStop.getName());
        }
        else
        {
            return busStop;
        }
    }

    /**
     * @return reference to the terminus.
     */
    public Terminus getTerminus()
    {
        return (Terminus) busStops.get(busStops.size() - 1);
    }

    /**
     * Searches for a bus stop with given name.
     * 
     * @param name
     *            - the name of the bus stop to be found.
     * @return bus stop of given id.
     * 
     * @throws NoSuchBusStopException
     *             - when there is no bus stop with given name.
     */
    public BusStop getBusStopByName(String name) throws NoSuchBusStopException
    {
        for (BusStop busStop : busStops)
        {
            if (busStop.getName() == name)
            {
                return busStop;
            }
        }
        throw new NoSuchBusStopException(name);
    }
}
