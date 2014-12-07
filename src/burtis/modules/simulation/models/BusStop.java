package burtis.modules.simulation.models;

import burtis.common.events.Simulation.BusArrivesAtBusStopEvent;
import burtis.common.events.Simulation.BusStopsListEvent;
import burtis.modules.simulation.Simulation;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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
    
    public static void addTerminus(String name) {
        if(!hasTerminus()) {
            busStops.add(new Terminus(name));
        }
    }
    
    /**
     * Checks if line already has terminus.
     * @return has terminus
     */
    private static boolean hasTerminus() {
        for(BusStop busStop : busStops) {
            if(busStop instanceof Terminus) {
                return true;
            }
        }
        return false;
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
        Simulation.client.send(new BusArrivesAtBusStopEvent(
                Simulation.simulationModuleConfig.getModuleName(),
                bus.getId(), 
                busStop.getId()));
    }

}
