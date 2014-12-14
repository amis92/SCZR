package burtis.modules.passengers;

import burtis.common.constants.SimulationModuleConsts;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of bus in passengers module.
 * 
 * @author Mikołaj Sowiński
 */
public class Bus {
    
    private static final List<Bus> buses = new ArrayList<>();
    
    private enum State {
        ATBUSSTOP, RUNNING, WAITING
    }
    
    private final int id;
    
    private State state;
    
    private int nextBusStopId;
        
    private final List<Passenger> passengers = new ArrayList<>();

    private Bus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public int getNextBusStopId() {
        return nextBusStopId;
    }
    
    public int getFreePlaces() {
        return SimulationModuleConsts.BUS_CAPACITY - passengers.size();
    }
    
    public static Bus add(int busId) {
        Bus bus = new Bus(busId);
        buses.add(bus);
        return bus;
    }

    @Override
    public String toString() {
        return "Bus{" + "id=" + id + "," + passengers.size() + " passengers}";
    }
    
    void arrive() {
        state = State.ATBUSSTOP;
    }
    
    void waiting() {
        state = State.WAITING;
    }

    void depart() {
        EventBuilder.addDepartingBus(this);
        state = State.RUNNING;
    }
    
    void resetBus() {
        passengers.clear();
        state = State.RUNNING;
    }
       
    public static Bus getBus(int id) {
        for(Bus bus : buses) {
            if(bus.getId() == id) return bus;
        }
        
        return null;
    }
    
    
    
    
    
    
    
}
