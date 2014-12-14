package burtis.common.mockups;

import burtis.modules.simulation.Simulation;
import burtis.modules.simulation.models.Bus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class MockupBus implements Serializable{
    private ArrayList<MockupPassenger> passengerList;
    private final String currentBusStop;
    private final int lengthPassed; // Posiiton in % of total line length 0..100
    private final Integer Id;
    private final MockupBusState oldBusState;
    private final Bus.State busState;

    public MockupBus(Integer Id) {
        this.passengerList = new ArrayList<MockupPassenger>();
        this.currentBusStop = null; //bus.getCurrentBusStop().getNAME();
        this.lengthPassed = 0; //bus.getCurrentBusStop().getRoute().getLength() - bus.getToNextStop().getValue();
        this.Id = Id;
        this.oldBusState = MockupBusState.ON_BUS_STOP;
        this.busState = Bus.State.BUSSTOP;
    }
    /*
    public MockupBus(Bus bus) {
        this.Id = bus.getId();
        this.currentBusStop = bus.getCurrentBusStop().getName();
        this.lengthPassed = bus.getPosition()*100/Simulation.getInstance().getLineLength();
        this.busState = bus.getState();
        //if(this.busState == Bus.State.DEPOT || this.busState == Bus.State.TERMINUS)
        
    }*/

    public ArrayList<MockupPassenger> getPassengerList() {
        return passengerList;
    }

    public String getCurrentBusStop() {
        return currentBusStop;
    }

    public int getLengthPassed() {
        return lengthPassed;
    }
/*
    public MockupBusState getState(){
        return busState;
    }
*/
	public Integer getId() {
		// TODO Auto-generated method stub
		return Id;
	}

    public void setPassengerList(ArrayList<MockupPassenger> passengerList)
    {
        this.passengerList = passengerList;
    }
}
