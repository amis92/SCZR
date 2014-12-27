package burtis.modules.passengers;

import java.util.ArrayList;
import java.util.List;

import burtis.common.events.SimulationEvent;
import burtis.common.events.Simulator.BusDeparturesEvent;

/**
 * Holds events to be sent at the end of the iteration.
 * 
 * @author Mikołaj Sowiński
 */
public class EventBuilder {
    
    /**
     * Array of buses that depart at the beginning of the next iteration.
     */
    private static final List<Bus> departingBuses = new ArrayList<>();
    
    /**
     * Adds bus to the list of the buses departing at the beginning of next iteration.
     * @param bus 
     */
    public static void addDepartingBus(Bus bus) {
        departingBuses.add(bus);
    }
    
    /**
     * Creates a list of events to be sent and clears events lists.
     * @param sender name of the sender module
     * @return list of BusDepartEvent
     */
    public static List<SimulationEvent> getEvents(String sender) {
        
        List<SimulationEvent> eventList = new ArrayList<>();
        
        departingBuses.forEach((Bus bus) -> {
            eventList.add(new BusDeparturesEvent(sender, bus.getId(), bus.getNextBusStopId()));
        });
        departingBuses.clear();
        
        
        return eventList;
        
    }
    
}
