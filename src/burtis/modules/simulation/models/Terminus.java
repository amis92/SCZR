package burtis.modules.simulation.models;

import java.util.LinkedList;
import java.util.List;

public class Terminus extends BusStop
{
    
    private static final List<Bus> buses = new LinkedList<>();

    public Terminus(String name) {
        super(0, name);
    }
    
    public static void enqueueBus(Bus bus) {
        buses.add(bus);
    }
    
}
