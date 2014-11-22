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
    
    private final Queue waitingBuses  = new LinkedList<WaitingBus>();
    
    private class WaitingBus 
    {
        private final int id;
        
        public WaitingBus(int id) { this.id = id; }
        public int id() { return id; }
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
    
    public String getState() {
        if(waitingBuses.size()>0) {
            return "BUSSTOP: " + name + " POS: " + position + " WAITING: " 
                + waitingBuses.size() + " ACTIVE: " + ((WaitingBus) waitingBuses.element()).id();
        }
        else {
            return "BUSSTOP: " + name + " POS: " + position + " WAITING: 0";
        }
    }
}
