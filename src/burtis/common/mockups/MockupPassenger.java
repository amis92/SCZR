package burtis.common.mockups;

import java.io.Serializable;

public class MockupPassenger implements Serializable{
    private final int Id;
    private String depot;
    private String destination;

    public MockupPassenger(MockupPassenger passenger) {
        this.Id = passenger.getId();
        this.destination = passenger.getDestination();
        this.destination = passenger.getDepot();
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