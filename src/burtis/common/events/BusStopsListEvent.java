package burtis.common.events;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mikołaj Sowiński
 */
public class BusStopsListEvent extends SimulationEvent {
   
    private final List<BusStopInfo> busStops = new ArrayList<>();
    
    public BusStopsListEvent(String sender, String[] recipients, List<BusStopInfo> busStops) {
        super(sender, recipients);
        busStops.addAll(busStops);
    }
    
}
