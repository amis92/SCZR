package mockup;

import model.Bus;
import model.Passenger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by ppeczek on 2014-05-28.
 */
public class MockupBus implements Serializable{
    private final ArrayList<MockupPassenger> passengerList;
    private final String currentBusStop;
    private final int lengthPassed;
    private final int ID;
    private final MockupBusState busState;
    private final int busStopDistace;

    public MockupBus(final Bus bus) {
        this.passengerList = new ArrayList<MockupPassenger>();
        for(LinkedList<Passenger> linkedList: bus.getPassengerList().values()) {
            for(Passenger passenger: linkedList){
                this.passengerList.add(new MockupPassenger(passenger));
            }
        }
        this.currentBusStop = bus.getCurrentBusStop().getNAME();
        this.busStopDistace = bus.getToNextStop().getValue();
        this.lengthPassed = bus.getCurrentBusStop().getRoute().getLength() - bus.getToNextStop().getValue();
        this.ID = bus.getID();
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
        }
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


    public int getID() {
        return ID;
    }
}
