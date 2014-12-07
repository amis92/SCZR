package burtis.modules.simulation.models;

import java.util.LinkedList;
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
    
    private final Queue waitingBuses  = new LinkedList<Integer>();
    
        private int processedBusId;

    /**
     * Get the value of processedBusId
     *
     * @return the value of processedBusId
     */
    public int getProcessedBusId() {
        return processedBusId;
    }

    /**
     * Set the value of processedBusId
     *
     * @param processedBusId new value of processedBusId
     */
    public void setProcessedBusId(int processedBusId) {
        this.processedBusId = processedBusId;
    }

    
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
    
    public Queue getWaitingBuses() {
        return waitingBuses;
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
    
    public String getState() {
        if(waitingBuses.size()>0) {
            return "BUSSTOP: " + name + " POS: " + position + " WAITING: " 
                + waitingBuses.size() + " ACTIVE: " + waitingBuses.element();
        }
        else {
            return "BUSSTOP: " + name + " POS: " + position + " WAITING: 0";
        }
    }
    
    public void enqueueBus(int busId) {
        this.waitingBuses.add(busId);
    }

}
