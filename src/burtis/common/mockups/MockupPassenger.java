package burtis.common.mockups;

import burtis.modules.passengers.Passenger;
import java.io.Serializable;

public class MockupPassenger implements Serializable{
    private int Id;
    private String depot; // origin
    private String destination;
    private long waitingTime;

    public MockupPassenger(Integer Id, String destination, String depot) {
        this.Id = Id;
        this.depot = depot;
        this.destination = destination;
    }
    
    public MockupPassenger(Passenger passenger) {
        this.Id = passenger.getId();
        this.depot = passenger.getOrigin().getName();
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
        return depot;
    }
}