package burtis.modules.simulation.models;

import java.util.LinkedList;
import java.util.List;

public class Terminus extends BusStop
{
    
    private static final List<Bus> buses = new LinkedList<>();
    private static long toTheNextDeparture = 0;

    public Terminus(int position, String name) {
        super(position, name);
    }
    
    public static void enqueueBus(Bus bus) {
        buses.add(bus);
    }
    
    /**
     * Departs buses at given intervals.
     */
    public static void departBus() {
        if(toTheNextDeparture == 0) {
            
        };
    }
    
    
    

    
}
