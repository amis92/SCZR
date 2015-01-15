package burtis.common.mockups;

import java.io.Serializable;

import burtis.modules.passengers.model.Passenger;

public class MockupPassenger implements Serializable
{
    private static final long serialVersionUID = 6872262177092264743L;
    private int id;
    private String origin;
    private String destination;
    private long waitingTime;

    public MockupPassenger(Integer Id, String origin, String destination)
    {
        this.id = Id;
        this.destination = destination;
        this.origin = origin;
        this.waitingTime = 0;
    }

    public MockupPassenger(Passenger passenger)
    {
        this.id = passenger.getId();
        this.origin = passenger.getOrigin().getName();
        this.destination = passenger.getDestination().getName();
        this.waitingTime = passenger.getWaitingTime();
    }

    public int getId()
    {
        return id;
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

    @Override
    public String toString()
    {
        String format = "%d\tfrom: %s\t to: %s";
        return String.format(format, id, origin, destination);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof MockupPassenger))
        {
            return false;
        }
        MockupPassenger other = (MockupPassenger) obj;
        return other.id == id;
    }
}
