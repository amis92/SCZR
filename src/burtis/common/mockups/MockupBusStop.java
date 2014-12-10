package burtis.common.mockups;

import java.util.ArrayList;
import java.util.List;

public class MockupBusStop {
    private final ArrayList<MockupPassenger> passengerList;
    private final String busStopName;
	
	public MockupBusStop(String busStopName) {
		this.passengerList = new ArrayList<MockupPassenger>();
		this.busStopName = busStopName;
		
		
	}
	
	public List<MockupPassenger> getPassengers()
	{
	    return passengerList;
	}

	public int getPassengerCount()
	{
	    return passengerList.size();
	}
}
