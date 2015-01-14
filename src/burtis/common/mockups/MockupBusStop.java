package burtis.common.mockups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MockupBusStop implements Serializable
{
    private static final long serialVersionUID = 1L;
    private ArrayList<MockupPassenger> passengerList;
    private final String busStopName;

    public MockupBusStop(String busStopName)
    {
        this.passengerList = new ArrayList<MockupPassenger>();
        this.busStopName = busStopName;
    }

    public MockupBusStop(ArrayList<MockupPassenger> passengerList,
            String busStopName)
    {
        this.passengerList = passengerList;
        this.busStopName = busStopName;
    }

    public List<MockupPassenger> getPassengers()
    {
        return passengerList;
    }

    public String getName()
    {
        return busStopName;
    }

    public ArrayList<MockupPassenger> getPassengerList()
    {
        return passengerList;
    }

    public void setPassengerList(ArrayList<MockupPassenger> passengerList)
    {
        this.passengerList = passengerList;
    }

    public int getPassengerCount()
    {
        return passengerList.size();
    }

    public String toString()
    {
        String mockupString = "\tBus stop: " + busStopName + "\n";
        if (passengerList.size() > 0)
        {
            mockupString += "\t\tPas: ";
            for (MockupPassenger passengerMockup : passengerList)
            {
                mockupString += passengerMockup.getId() + " ";
            }
            mockupString += "\n";
        }
        return mockupString;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof MockupBusStop))
        {
            return false;
        }
        MockupBusStop other = (MockupBusStop) obj;
        return other.busStopName.equals(other);
    }
}
