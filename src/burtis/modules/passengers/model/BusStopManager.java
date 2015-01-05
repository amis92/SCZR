package burtis.modules.passengers.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import burtis.common.constants.SimulationModuleConsts;
import burtis.modules.passengers.Managers;
import burtis.modules.passengers.exceptions.NoSuchBusStopException;

/**
 * Bus stop manager for Passenger Module.
 * 
 * @author Mikołaj Sowiński
 *
 */
public class BusStopManager
{

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
    private final Managers managers;
    
    /**
     * Constructor.
     */
    public BusStopManager(Managers managers) {
        
        this.managers = managers;
        managers.setBusStopManager(this);
                
        // Build list of bus stops from the default configuration
        for(Entry<Integer,String> busStopData : SimulationModuleConsts.getDefaultBusStops()) {
            busStops.add(new BusStop(
                    busStopData.getKey(), 
                    busStopData.getValue(),
                    managers));
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
     * @param id id of the bus stop
     * @throws NoSuchBusStopException 
     */
    public BusStop getBusStopById(int id) throws NoSuchBusStopException {
        
        for(BusStop busStop : busStops) {
            if(busStop.getId() == id) return busStop;
        }
        
        throw new NoSuchBusStopException(new Integer(id).toString());
    }
    
    /**
     * Returns random bus stop, except last one.
     * 
     * @return random bus stop
     */
    public BusStop getRandomBusStop() {
        int busStopIndex = randomGenerator.nextInt(busStops.size()-1);
        return busStops.get(busStopIndex);
    }
    
    /**
     * Returns random destination bus stop from range of next from origin to the
     * last in the line.
     * 
     * @param origin origin bus stop
     * @return destination bus stop
     */
    public BusStop getRandomNextBusStop(BusStop origin) {
        int originIndex = busStops.indexOf(origin);
        int nextBusStopIndex = randomGenerator.nextInt(busStops.size()-originIndex-1) +originIndex +1;
        return busStops.get(nextBusStopIndex);
    }
    
    /**
     * Returns number of passengers waiting at the bus stop of given id.
     * 
     * @param busStopId bus stop id
     * @return number of waiting passengers
     * 
     * @throws NoSuchBusStopException 
     */
    public int waitingPassengers(int busStopId) throws NoSuchBusStopException {
        return getBusStopById(busStopId).waitingPassengers();
    }
    
    /**
     * Generates map where key is bus stop id and value boolean that is true if there are any passengers
     * waiting or false otherwise.
     * 
     * @return map of bus stops with waiting passengers
     */
    public Map<Integer, Boolean> getWaitingPassengersMap() 
    {
        Map<Integer, Boolean> map = new HashMap<>();
                
        for(BusStop busStop : busStops) {
            map.put(busStop.getId(), busStop.waitingPassengers() != 0);
        }
        
        return map;
    }
 
}
