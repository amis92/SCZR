package burtis.common.mockups;

import burtis.modules.passengers.Passenger;
import java.io.Serializable;

public class MockupPassenger implements Serializable{
    private static final long serialVersionUID = 6872262177092264743L;
    private final int Id;
    private String depot; // origin
    private String destination;
    private final long waitingTime;

    public MockupPassenger(MockupPassenger passenger) {
        this.Id = passenger.getId();
        this.destination = passenger.getDestination();
        this.destination = passenger.getDepot();
        this.waitingTime = passenger.waitingTime;
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
    
    public long getWaitingTime() {
        return waitingTime;
    }
}