package burtis.common.mockups;

import java.io.Serializable;
import java.util.ArrayList;

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
     * Name of the current bus stop the bus in enqueued to.
     * This field can be null.
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
     * Bus state as given in {@link Bus#State}.
     */
    private final Bus.State busState;
    
    /**
     * Constructor from the simulation {@link Bus} object.
     * 
     * @param bus bus object
     */
    public MockupBus(Bus bus) {
        
        this.Id = bus.getId();
        this.passengerList = new ArrayList<MockupPassenger>();
        if(bus.getClosestBusStop() == null) {
            this.currentBusStop = null;
        }
        else {
            this.currentBusStop = bus.getClosestBusStop().getName();
        }
        this.lengthPassed = bus.getPosition();
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
    
    /**
     * @return list of passengers mockups
     */
    public ArrayList<MockupPassenger> getPassengerList()
    {
        return passengerList;
    }

    /**
     * @return the lengthPassed
     */
    public int getLengthPassed()
    {
        return lengthPassed;
    }

    /**
     * @param lengthPassed the lengthPassed to set
     */
    public void setLengthPassed(int lengthPassed)
    {
        this.lengthPassed = lengthPassed;
    }

    /**
     * @return the currentBusStop
     */
    public String getCurrentBusStop()
    {
        return currentBusStop;
    }

    /**
     * @return the id
     */
    public Integer getId()
    {
        return Id;
    }

    /**
     * @return the busState
     */
    public Bus.State getBusState()
    {
        return busState;
    }

    /**
     * Sets list of passengers of the bus.
     * 
     * @param list of passengers to set
     */
    public void setPassengerList(ArrayList<MockupPassenger> passengerList)
    {
        this.passengerList = passengerList;
    }
    
    @Override
    public String toString() {
        String busString = 
                "\tBus ID:" + Id + ", Pos:" + lengthPassed + ", S:" + busState + "\n\t\tPas: ";
        
        for(MockupPassenger passenger : passengerList) {
            busString += passenger.getId() + " ";
        }
        
        busString += "\n";
        
        return busString;    
    }

}
