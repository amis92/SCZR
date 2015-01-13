package burtis.common.events.Simulator;

import java.io.Serializable;

public class BusDepartureInfo implements Serializable {

    private static final long serialVersionUID = -450213622696573158L;
    public final int busId; 
    public final int nexBusStopId;
    
    public BusDepartureInfo(int busId, int nextBusStopId) {
        this.busId = busId;
        this.nexBusStopId = nextBusStopId;
    }
}
