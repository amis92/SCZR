package burtis.modules.passengers.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.Random;

import burtis.common.constants.SimulationModuleConsts;
import burtis.modules.passengers.Managers;
import burtis.modules.passengers.PassengerModule;
import burtis.modules.passengers.exceptions.NoSuchBusStopException;

/**
 * Bus stop manager for Passenger Module.
 * 
 * @author Mikołaj Sowiński
 *
 */
public class BusStopManager
{
    private Logger logger = Logger
            .getLogger(PassengerModule.class.getName());
    /**
     * List of available bus stops.
     */
    private final List<BusStop> busStops = new ArrayList<>();
    /**
     * Random passenger generator.
     */
    private final Random randomGenerator = new Random();

    /**
     * Reference to managers container.
     */
    // private final Managers managers;
    /**
     * Constructor.
     */
    public BusStopManager(Managers managers)
    {
        // this.managers = managers;
        managers.setBusStopManager(this);
        // Build list of bus stops from the default configuration
        for (Entry<Integer, String> busStopData : SimulationModuleConsts
                .getDefaultBusStops())
        {
            busStops.add(new BusStop(busStopData.getValue(), busStopData.getKey(), managers));
        }
    }

    /**
     * @return the busStops
     */
    public List<BusStop> getBusStops()
    {
        return busStops;
    }
    /**
     * Returns bus stop of specified id.
     * 
     * @param id
     *            id of the bus stop
     * @throws NoSuchBusStopException
     */
    public BusStop getBusStopByName(String name) throws NoSuchBusStopException
    {
        for (BusStop busStop : busStops)
        {
            if (busStop.getName().equalsIgnoreCase(name))
                return busStop;
        }
        logger.warning(String.format("Bus stop name = %s not found", name));
        throw new NoSuchBusStopException(name);
    }

    

    /**
     * Returns random bus stop, except last one.
     * 
     * @return random bus stop
     */
    public BusStop getRandomBusStop()
    {
        int busStopIndex = randomGenerator.nextInt(busStops.size() - 1);
        return busStops.get(busStopIndex);
    }

    /**
     * Returns random destination bus stop from range of next from origin to the
     * last in the line.
     * 
     * @param origin
     *            origin bus stop
     * @return destination bus stop
     */
    public BusStop getRandomNextBusStop(BusStop origin)
    {
        int originIndex = busStops.indexOf(origin);
        int nextBusStopIndex = randomGenerator.nextInt(busStops.size()
                - originIndex - 1)
                + originIndex + 1;
        return busStops.get(nextBusStopIndex);
    }

    /**
     * Returns number of passengers waiting at the bus stop of given id.
     * 
     * @param busStopName
     *            bus stop name
     * @return number of waiting passengers
     * 
     * @throws NoSuchBusStopException
     */
    public int waitingPassengers(String busStopName) throws NoSuchBusStopException
    {
        return getBusStopByName(busStopName).waitingPassengers();
    }

    /**
     * Generates map where key is bus stop id and value boolean that is true if
     * there are any passengers waiting or false otherwise.
     * 
     * @return map of bus stops with waiting passengers
     */
    public Map<String, Boolean> getWaitingPassengersMap()
    {
        Map<String, Boolean> map = new HashMap<>();
        for (BusStop busStop : busStops)
        {
            map.put(busStop.getName(), busStop.waitingPassengers() != 0);
        }
        return map;
    }
    
    /**
     * Iterates over all bus stops and calls {@link BusStop#nextBus()} on every bus stop.
     */
    public void callNextBus() {
        for(BusStop busStop : busStops) {
            busStop.nextBus();
        }
    }
    
    @Override
    public String toString() {
        String out = "";
        for(BusStop busStop : busStops) {
            out += busStop.toString();
        }
        return out;
    }
}
