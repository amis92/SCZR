package burtis.modules.simulation.models;

import java.util.LinkedList;
import java.util.Queue;

public class Depot
{

    private static final Queue<Bus> buses = new LinkedList<>();
    
    public static void putBus(Bus bus) {
        buses.add(bus);
    }
    
    public static Bus getBus(int busId) {
        for(Bus bus : buses) {
            if(bus.getId()==busId) {
                buses.remove(bus);
                return bus;
            }
        }
        return null;
    }
    
    public static Bus getBus() {
        return buses.poll();
    }
  
}
