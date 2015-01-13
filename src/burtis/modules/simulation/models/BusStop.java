package burtis.modules.simulation.models;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import burtis.common.events.Simulator.BusStopsListEvent;

public class BusStop
{
    
    /**
     * Generates unique ids for bus stops.
     */
    private static class IDGenerator
    {
        private static int lastId = 0;

        public static int getNextId()
        {
            return lastId++;
        }
    }    
    
    /**
     * Bus stop id.
     */
    private final int id;
    
    /**
     * Bus stop name.
     */
    private final String name;
    
    /**
     * Position expressed in meters.
     */
    private final int position;
    
    /**
     * FIFO queue of buses waiting to the bus stop.
     * 
     * None of the buses here is currently AT the bus stop.
     */
    private final Queue<Bus> waitingBuses = new LinkedList<>();
    
    /**
     * Bus that is currently at the bus stop.
     */
    private Bus currentBus;
        
    /**
     * Constructor.
     * 
     * Assigns unique id for the new bus stop.
     * Name parameter can be null, then name in form of
     * "Bus stop <id>" will be set.
     * 
     * @param position
     * @param name
     */
    public BusStop(int position, String name)
    {
        this.id = IDGenerator.getNextId();
        this.position = position;
        if (name == null)
        {
            this.name = "Bus stop " + this.id;
        }
        else
        {
            this.name = name;
        }
    }

/* ##############################################
 * GETTERS AND SETTERS
 * ########################################### */

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public int getPosition()
    {
        return position;
    }
    
/* ##############################################
 * END OF GETTERS AND SETTERS
 * ########################################### */

    /**
     * Enqueues bus to the bus stop. 
     * Bus is put into FIFO queue.
     */
    public void enqueueBus(Bus bus)
    {
        waitingBuses.add(bus);
    }

    
}
