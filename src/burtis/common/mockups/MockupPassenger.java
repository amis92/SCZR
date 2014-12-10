package burtis.common.mockups;

import burtis.modules.passengers.Passenger;
import java.io.Serializable;

public class MockupPassenger implements Serializable{
    private int Id;
    private String origin;
    private String destination;
    private final long waitingTime;

    public MockupPassenger(Integer Id, String origin, String destination) {
        this.Id = Id;
        this.destination = destination;
        this.origin = origin;
        this.waitingTime = 0;
    }
    
    public MockupPassenger(Passenger passenger) {
        this.Id = passenger.getId();
        this.origin = passenger.getOrigin().getName();
        this.destination = passenger.getDestination().getName();
        this.waitingTime = passenger.getWaitingTime();
    }

    public int getId() {
        return Id;
    }

    public String getDestination() {
        return destination;
    }
    
    public String getDepot() {
        return origin;
    }
}