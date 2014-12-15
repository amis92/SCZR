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

    public MockupBusStop(ArrayList<MockupPassenger> passengerList, String busStopName) {
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
    
    public void print() {
        System.out.println("| Bus stop: " + busStopName);
        if(passengerList.size() > 0) {
            System.out.println("| Passengers:\n-------------");
            for(MockupPassenger passengerMockup : passengerList) {
                passengerMockup.print();
            }
        }
    }
    
}
