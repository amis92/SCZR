package burtis.common.mockups;

import java.io.Serializable;

import burtis.modules.passengers.model.Passenger;

public class MockupPassenger implements Serializable
{
    private static final long serialVersionUID = 6872262177092264743L;
    private int Id;
    private String origin;
    private String destination;
    private long waitingTime;

    public MockupPassenger(Integer Id, String origin, String destination)
    {
        this.Id = Id;
        this.destination = destination;
        this.origin = origin;
        this.waitingTime = 0;
    }

    public MockupPassenger(Passenger passenger)
    {
        this.Id = passenger.getId();
        this.origin = passenger.getOrigin().getName();
        this.destination = passenger.getDestination().getName();
        this.waitingTime = passenger.getWaitingTime();
    }

    public int getId()
    {
        return Id;
    }

    public String getDestination()
    {
        return destination;
    }

    public String getDepot()
    {
        return origin;
    }

    public long getWaitingTime()
    {
        return waitingTime;
    }
    
    public void print() {
        System.out.println("| * " + Id + " from: " + origin + " to: " + destination);
    }
}
