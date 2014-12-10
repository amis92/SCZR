package burtis.common.mockups;

import java.util.ArrayList;

public class MockupBusStop {
    private final ArrayList<MockupPassenger> passengerList;
    private final String busStopName;
	
	public MockupBusStop(String busStopName) {
		this.passengerList = new ArrayList<MockupPassenger>();
		this.busStopName = busStopName;
		
		
	}

}
