package burtis.common.mockups;

import java.util.ArrayList;

public class MockupBusStop {
    private ArrayList<MockupPassenger> passengerList;
    private final String busStopName;
	
	public MockupBusStop(String busStopName) {
		this.passengerList = new ArrayList<MockupPassenger>();
		this.busStopName = busStopName;	
		
	}

    public String getName() {
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
}
