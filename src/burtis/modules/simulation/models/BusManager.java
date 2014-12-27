package burtis.modules.simulation.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.events.Simulator.BusDeparturesEvent;
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
     * List of bus arrivals.
     */
    private final List<Integer> busArrivalsList = new ArrayList<>();

    /**
     * Constructor.
     * 
     * @param busStopManager reference to BusStopManager
     * @param numberOfBuses number of buses to create
     */
    public BusManager(BusStopManager busStopManager, int numberOfBuses, int busCapacity) {
        this.busStopManager = busStopManager;
        
        Bus bus;
        for(int i=0; i<numberOfBuses; i++) {
            bus = new Bus(busCapacity, busStopManager, this);
            buses.put(bus.getId(), bus);
        }
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
        Bus newBus = new Bus(capacity, busStopManager, this);
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
    
    /**
     * Builds list of ids of bus stops that are to be 
     * queried for waiting passengers.
     */
    public List<Integer> getBusStopsIdsList() {
        
        Set<Integer> busStopsList = new HashSet<>();
        for(Bus bus : buses.values()) {
            if(bus.getBusStopQueryRequest() != null) {
                busStopsList.add(bus.getBusStopQueryRequest().getId());
            }
        }
        return new ArrayList<Integer>(busStopsList);
    }
    
    /**
     * Sets response bits in bus objects and triggers response processing
     * on every bus that requested information.
     * 
     * @throws NoSuchBusStopException 
     */
    public void processWaitingPassengersQueryResponse(Map<Integer,Boolean> response) throws NoSuchBusStopException {
        
        for(Bus bus : buses.values()) {
            // If the bus was expecting an answer...
            if(bus.getBusStopQueryRequest() != null) {
                // set an answer 
                bus.setQueryResult(response.get(bus.getBusStopQueryRequest().getId()));
                // and make bus process it
                bus.processQueryResult();
            }
        }
        
    }
    
    /**
     * Adds bus to the list of buses that arrives at the bus stop in current iteration.
     * 
     * @param bus bus to be added
     */
    public void addBusArrival(Bus bus) {
        
        busArrivalsList.add(bus.getId());
        
    }
    
    /**
     * Returns list of IDs of buses that arrives at the bus stop in the current iteration.
     * 
     * List of arrivals is cleared upon retrieval.
     */
    public List<Integer> getBusArrivalsList() {
        
        List<Integer> listCopy = busArrivalsList;
        
        busArrivalsList.clear();    
        
        return listCopy;
    } 
    
    /**
     * Processes bus departures list.
     * 
     * @throws NoSuchBusStopException 
     */
    public void processBusDeparturesList(List<BusDeparturesEvent.BusDepartureInfo> departureList) throws NoSuchBusStopException {
        
        for(BusDeparturesEvent.BusDepartureInfo departureInfo : departureList) {
            departBus(departureInfo.busId, departureInfo.nexBusStopId);
        }
    }
    
    /**
     * Calls {@link Bus#depart(BusStop)} at bus of given id.
     * 
     * @param busId id of bus that is to be departed
     * @param nextBusStopId id of next bus stop that is requested by passengers
     * 
     * @throws NoSuchBusStopException 
     */
    private void departBus(int busId, int nextBusStopId) throws NoSuchBusStopException {
        getBusById(busId).depart(busStopManager.getBusStopById(nextBusStopId));
    }
    
    /**
     * Returns list of @{link {@link MockupBus} objects corresponding to the current state of the buses.
     * 
     * @return List<MockupBus> list of bus mockups
     */
    public List<MockupBus> getBusMockups() {
        
        List<MockupBus> list = new ArrayList<>();
        
        for(Bus bus : buses.values()) {
            list.add(new MockupBus(bus));
        }
        
        return list;
        
    }
}
