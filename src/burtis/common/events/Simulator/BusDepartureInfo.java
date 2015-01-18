package burtis.common.events.Simulator;

import java.io.Serializable;

public class BusDepartureInfo implements Serializable {

    private static final long serialVersionUID = -450213622696573158L;
    public final int busId; 
    public final String nextBusStopName;
    
    public BusDepartureInfo(int busId, String nextBusStopName) {
        this.busId = busId;
        this.nextBusStopName = nextBusStopName;
    }
    
    @Override
    public String toString() {
        return "Bus: " + busId + " next bus stop: " + nextBusStopName;
    }
}
