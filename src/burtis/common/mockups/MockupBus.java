package burtis.common.mockups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class MockupBus implements Serializable{
    private ArrayList<MockupPassenger> passengerList;
    private final String currentBusStop;
    private final int lengthPassed;
    private final Integer Id;
    private final MockupBusState busState;
    private final int busStopDistace;

    public MockupBus(Integer Id) {
        this.passengerList = new ArrayList<MockupPassenger>();
        this.currentBusStop = null; //bus.getCurrentBusStop().getNAME();
        this.busStopDistace = 0; //bus.getToNextStop().getValue();
        this.lengthPassed = 0; //bus.getCurrentBusStop().getRoute().getLength() - bus.getToNextStop().getValue();
        this.Id = Id;
        this.busState = MockupBusState.ON_BUS_STOP;
        
        /*
        switch(bus.getState()) {
            case READY_TO_GO:
                this.busState = MockupBusState.ON_BUS_STOP;
                break;
            case RUNNING:
                this.busState = MockupBusState.RUNNING;
                break;
            case WAITING:
                this.busState = MockupBusState.WAITING;
                break;
            case PUT_OUT_ALL:
                this.busState = MockupBusState.ON_BUS_STOP;
                break;
            case PUT_OUT:
                this.busState = MockupBusState.ON_BUS_STOP;
                break;
            case TAKE_IN:
                this.busState = MockupBusState.ON_BUS_STOP;
                break;
            case FINISHED:
                this.busState = MockupBusState.RUNNING;
                break;
            case HAVING_BREAK:
                this.busState = MockupBusState.ON_BUS_STOP;
                break;
            default:
                this.busState = MockupBusState.ON_BUS_STOP;
        }*/
    }

    public ArrayList<MockupPassenger> getPassengerList() {
        return passengerList;
    }

    public String getCurrentBusStop() {
        return currentBusStop;
    }

    public int getLengthPassed() {
        return lengthPassed;
    }

    public int getBusStopDistace() {
        return busStopDistace;
    }

    public MockupBusState getState(){
        return busState;
    }

	public Integer getId() {
		// TODO Auto-generated method stub
		return Id;
	}

    public void setPassengerList(ArrayList<MockupPassenger> passengerList)
    {
        this.passengerList = passengerList;
    }
}
