package burtis.modules.simulation.models;

import java.util.LinkedList;
import java.util.List;

import burtis.common.events.Simulator.BusArrivesAtBusStopEvent;
import burtis.common.events.Simulator.BusStopsListEvent;
import burtis.modules.simulation.Simulation;

public class BusStop
{
    private static final String namePrefix = "Przystanek ";
       
    private final int id;
    /**
     * Position expressed in meters.
     */
    private final int position;
    private final String name;

    private static final List<BusStop> busStops = new LinkedList<>();
    
    /**
     * Generates unique ids for bus stops.
     */
    private static class IDGenerator
    {
        private static int lastId = 0;
        
        public static int getNextId(){
            return lastId++;
        }   
    }
    
    public BusStop(int position, String name) 
    {
        this.id = IDGenerator.getNextId();
        this.position = position;
        if(name==null) {
            this.name = namePrefix + "nr " + this.id;
        }
        else {
            this.name = name;
        }
    }
    
    /**
     * Adds bus stop with given parameters.
     * Name can be null. In such case default bus stop name will be assigned.
     * @param position position of bus stop
     * @param name bus stop name
     * @return newly created bus stop
     */
    public static BusStop add(int position, String name) {
        BusStop busStop = new BusStop(position, name);
        busStops.add(busStop);
        return busStop;
    }
    
    /**
     * Adds new bus stop object to the list of bus stops.
     * 
     * @param busStop bus stop to be added
     * @return bus stop given as parameter
     */
    public static BusStop add(BusStop busStop) {
        busStops.add(busStop);
        return busStop;
    }

        
    /**
     * Returns list of bus stops ready to be sent to the passenger module.
     * 
     * @return list of BusStopsListEvent.BusStop
     */
    public static List<BusStopsListEvent.BusStop> getBusStopsList() {
        List<BusStopsListEvent.BusStop> busStopsList = new LinkedList<>();
        
        for(BusStop busStop : busStops) {
            if(!(busStop instanceof Terminus)) {
                busStopsList.add(new BusStopsListEvent.BusStop(busStop.getId(), busStop.getName()));
            }
        }
        
        return busStopsList;
    }
    
    /**
     * Returns bus stop of given id.
     * 
     * @param id bus stop id
     * @return bus stop
     */
    public static BusStop getBusStopById(int id) {
        for(BusStop busStop : busStops) {
            if(busStop.getId() == id) return busStop;
        }
        return null;
    }
        
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getPosition() {
        return position;
    }

    /**
     * Enqueues bus to the bus stop.
     * Sends BusArrivesAtBusStopEvent to the passenger module.
     * @param bus bus to be enqueued
     * @param busStop bus stop bus to be queued to
     */
    public static void enqueueBus(Bus bus, BusStop busStop) {
        Simulation.getInstance().send(new BusArrivesAtBusStopEvent(
                Simulation.getInstance().getModuleConfig().getModuleName(),
                bus.getId(), 
                busStop.getId()));
    }
    
    /**
     * Returns bus stop closest to the given position.
     * If position is larger than the furthest bus stop, a final terminus is returned.
     * @param position searched position
     * @return closes bus stop
     */
    public static BusStop getClosestBusStop(int position) {
        
        for(BusStop busStop : busStops) {
            if(busStop.getPosition() > position) {
                return busStop;
            }
        }
        return busStops.get(busStops.size()-1);
        
    }

}
