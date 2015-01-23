package burtis.common.mockups;

import java.io.Serializable;
import java.util.ArrayList;

import burtis.common.constants.SimulationModuleConsts;
import burtis.modules.simulation.models.Bus;

/**
 * Mockup containing data of a single bus.
 * 
 * @author vanqyard, Mikołaj Sowiński
 *
 */
public class MockupBus implements Serializable
{
    private static final long serialVersionUID = 3835208126228698973L;
    /**
     * List of passengers mockups.
     */
    private ArrayList<MockupPassenger> passengerList;
    /**
     * Name of the current bus stop the bus in enqueued to. This field can be
     * null.
     */
    private final String currentBusStop;
    /**
     * Position of the bus in units.
     */
    private int lengthPassed;
    /**
     * Bus id.
     */
    private final Integer Id;
    /**
     * Bus state as given in {@link Bus.State}.
     */
    private final Bus.State busState;

    /**
     * Constructor from the simulation {@link Bus} object.
     * 
     * @param bus
     *            - the bus object.
     */
    public MockupBus(Bus bus)
    {
        this.Id = bus.getId();
        this.passengerList = new ArrayList<MockupPassenger>();
        this.currentBusStop = (bus.getCurrentBusStop() == null) ? "NULL" : bus
                .getCurrentBusStop().getName();
        this.lengthPassed = Math.round(((float) bus.getPosition())
                / SimulationModuleConsts.getLineLength() * 100.0F);
        this.busState = bus.getState();
    }

    public MockupBus(int id, int lengthPassed)
    {
        this.Id = id;
        this.passengerList = new ArrayList<MockupPassenger>();
        this.currentBusStop = null;
        this.lengthPassed = lengthPassed;
        this.busState = Bus.State.RUNNING;
    }

    public MockupBus(Integer id, int lengthPassed)
    {
        this.Id = id;
        this.passengerList = new ArrayList<MockupPassenger>();
        this.currentBusStop = null;
        this.lengthPassed = lengthPassed;
        this.busState = Bus.State.RUNNING;
    }

    public ArrayList<MockupPassenger> getPassengerList()
    {
        return passengerList;
    }

    public int getLengthPassed()
    {
        return lengthPassed;
    }

    public void setLengthPassed(int lengthPassed)
    {
        this.lengthPassed = lengthPassed;
    }

    public String getCurrentBusStop()
    {
        return currentBusStop;
    }

    public Integer getId()
    {
        return Id;
    }

    public Bus.State getBusState()
    {
        return busState;
    }

    public void setPassengerList(ArrayList<MockupPassenger> passengerList)
    {
        this.passengerList = passengerList;
    }

    @Override
    public String toString()
    {
        String format = "Bus Id: %s, Progress: %d, Bus State: %s, Current Bus Stop: %s\n\t";
        String busString;
        busString = String.format(format, Id, lengthPassed, busState,
                currentBusStop);
        for (MockupPassenger passenger : passengerList)
        {
            busString += passenger.getId() + " ";
        }
        busString += "\n";
        return busString;
    }

    public String toString(boolean noPassengersInfo)
    {
        if (!noPassengersInfo)
            return toString();
        String format = "Bus Id: %s, Progress: %d, Bus State: %s, Current Bus Stop: %s";
        String busStopName = currentBusStop == null ? "N/A" : currentBusStop;
        return String.format(format, Id.toString(), lengthPassed,
                busState.toString(), busStopName);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof MockupBus)
        {
            MockupBus other = (MockupBus) obj;
            return other.Id.equals(Id);
        }
        return false;
    }
}
