package burtis.common.mockups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import burtis.modules.passengers.Passenger;
import burtis.modules.simulation.Simulation;
import burtis.modules.simulation.models.Bus;

public class MockupBus implements Serializable
{
    private static final long serialVersionUID = 3835208126228698973L;
    private final ArrayList<MockupPassenger> passengerList;
    private final String currentBusStop;
    private final int lengthPassed; // Posiiton in % of total line length 0..100
    private final Integer Id;
    private final Bus.State busState;

    public MockupBus(Integer Id)
    {
        this.passengerList = new ArrayList<MockupPassenger>();
        this.currentBusStop = null; // bus.getCurrentBusStop().getNAME();
        this.lengthPassed = 0; // bus.getCurrentBusStop().getRoute().getLength()
                               // - bus.getToNextStop().getValue();
        this.Id = Id;
        this.busState = Bus.State.BUSSTOP;
    }

    public MockupBus(Bus bus, List<Passenger> passengerList)
    {
        this.Id = bus.getId();
        this.currentBusStop = bus.getCurrentBusStop().getName();
        this.lengthPassed = bus.getPosition() * 100
                / Simulation.getInstance().getLineLength();
        this.busState = bus.getState();
        this.passengerList = new ArrayList<MockupPassenger>(
                bus.getNumberOfPassengers());
        for (Passenger passenger : passengerList)
        {
            this.passengerList.add(new MockupPassenger(passenger));
        }
    }

    public ArrayList<MockupPassenger> getPassengerList()
    {
        return passengerList;
    }

    public String getCurrentBusStop()
    {
        return currentBusStop;
    }

    public int getLengthPassed()
    {
        return lengthPassed;
    }

    public Bus.State getState()
    {
        return busState;
    }

    public Integer getId()
    {
        // TODO Auto-generated method stub
        return Id;
    }
}
