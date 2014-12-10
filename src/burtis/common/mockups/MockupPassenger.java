package burtis.common.mockups;

import java.io.Serializable;

public class MockupPassenger implements Serializable{
    private final int Id;
    private String depot;
    private String destination;

    public MockupPassenger(Integer Id, String destination, String depot) {
        this.Id = Id;
        this.destination = destination;
        this.depot = depot;
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