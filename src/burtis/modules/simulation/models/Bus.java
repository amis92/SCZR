package burtis.modules.simulation.models;

import burtis.common.constants.SimulationModuleConsts;
import burtis.common.events.Passengers.WaitingPassengersEvent;
import burtis.common.events.Simulation.WaitingPassengersRequestEvent;
import burtis.common.events.SimulationEvent;
import burtis.modules.simulation.Simulation;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Bus
{
    
    private static final List<Bus> buses = new ArrayList<>();
    
    public enum State {
        DEPOT, BUSSTOP, RUNNING, TERMINUS
    }
    private State state;
   
    private final int id;
    
    /**
     * Maximum bus capacity.
     */
    private final int capacity;
    
    /**
     * Number of passengers in the bus.
     */
    private int numberOfPassengers;
    
    /**
     * Bus speed (units/iteration).
     */
    private final static int busSpeed = SimulationModuleConsts.BUS_SPEED;
    
    /**
     * Next bus stop defined by passengers.
     */
    private BusStop nextBusStop;
    
    /**
     * Next bus stop on the line.
     */
    private BusStop closestBusStop;
   
//    /**
//     * Departure iteration.
//     */
//    private long startAt;
    
    /**
     * Determines if bus should be moved to the depot after arriving to the terminus.
     */
    private boolean goToDepot;
    
    /**
     * Number of full line cycle.
     */
    private int cycle;
    
    /**
     * Number of cycles to be done by default.
     */
    private final int maxCycles = SimulationModuleConsts.BUS_MAX_CYCLES;
    
    /**
     * Current bus position.
     */
    private int position = 0;
    
    /**
     * Generates bus ids.
     */
    private static class IDGenerator
    {
        private static int lastId = 0;
        
        public static int getNextId(){
            return lastId++;
        }   
    }
    
    /**
     * Constructor of Bus class.
     * Buses are always created in DEPOT state.
     * @param capacity bus capacity
     */
    public Bus(int capacity) {
        id = IDGenerator.getNextId();
        state = State.DEPOT;
        this.capacity = capacity;
    }

    public int getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(int numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }

    public BusStop getNextBusStop() {
        return nextBusStop;
    }

    public void setNextBusStop(BusStop nextBusStop) {
        this.nextBusStop = nextBusStop;
    }

    public BusStop getClosestBusStop() {
        return closestBusStop;
    }

    public void setClosestBusStop(BusStop closestBusStop) {
        this.closestBusStop = closestBusStop;
    }
    
    
    
    
    /**
     * Get number of free places in the bus.
     * 
     * @return number of free places
     */
    public int getFreePlaces() {
        return capacity-numberOfPassengers;
    }



    /**
     * Get the value of state
     *
     * @return the value of state
     */
    public State getState() {
        return state;
    }

    /**
     * Set the value of state
     *
     * @param state new value of state
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Get the value of position
     *
     * @return the value of position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Set the value of position
     *
     * @param position new value of position
     */
    public void setPosition(int position) {
        this.position = position;
    }
    



    /**
     * Get the value of goToDepot
     *
     * @return the value of goToDepot
     */
    public boolean isGoToDepot() {
        return goToDepot;
    }

    /**
     * Set the value of goToDepot
     *
     * @param goToDepot new value of goToDepot
     */
    public void setGoToDepot(boolean goToDepot) {
        this.goToDepot = goToDepot;
    }

//    /**
//     * Get the value of startAt
//     *
//     * @return the value of startAt
//     */
//    public long getStartAt() {
//        return startAt;
//    }
//
//    /**
//     * Set the value of startAt
//     *
//     * @param startAt new value of startAt
//     */
//    public void setStartAt(long startAt) {
//        this.startAt = startAt;
//    }
    
    public int getId() {
        return id;
    }
    
    public String getStateString() {
        return "BUS: " + id + ", POS: " + position + " S: " + state;
    }
    
    public void depart() {
        state = State.RUNNING;
    }
    
    ////////////////////////////////
    //////// STATIC METHODS ////////
    ////////////////////////////////
    
    /** 
     * Returns bus of given id.
     * @param busId bus id
     * @return Bus
     */
    public static Bus getBusById(int busId) {
        for(Bus bus : buses) {
            if(bus.getId()==busId)
                return bus;
        }
        return null;
    }
    
    /**
     * Adds new bus of given capacity.
     * @param capacity bus capacity
     * @return newly created bus
     */
    public static Bus add(int capacity) {
        Bus newBus = new Bus(capacity);
        buses.add(newBus);
        return newBus;
    }
    
    /**
     * Updates bus positions according to its state.
     */
    public static void updatePositions() {
        
        for(Bus bus : buses) {
            
            // Running case
            if(bus.getState() == Bus.State.RUNNING) {            
                calculatePosition(bus);
            }
            
//            // Terminus case
//            else if(bus.getState() == Bus.State.TERMINUS) {
//                
//                // Check if it is time to go...?
//                if(Simulation.getCurrentCycle() >= bus.getStartAt()) {
//                    bus.setState(Bus.State.RUNNING);
//                    calculatePosition(bus);
//                }
//            }
        }
    }
    
    /**
     * Calculates next position of the bus.
     * If one reaches bus stop, registers it in the bus stop queue as well as 
     * changes its internal state to BUSSTOP.
     * @param bus 
     */
    private static void calculatePosition(Bus bus) {
        
        // Did we reach closest bus stop?
        BusStop closestBusStop = bus.getClosestBusStop();
        
        // If bus reaches bus stop in this iteration
        if(bus.getPosition() + busSpeed >= closestBusStop.getPosition()) {
            
            // If nearest bus stop is terminus we are at the terminus
            if(bus.getClosestBusStop() instanceof Terminus) {
                // Withdraw to the depot
                if(bus.goToDepot || bus.cycle == bus.maxCycles) {
                    bus.setState(Bus.State.DEPOT);
                    bus.setPosition(0);
                    Depot.putBus(bus);
                }
                // Enqueue to the terminus
                else {
                    bus.cycle++;
                    bus.setState(State.TERMINUS);
                    bus.setPosition(0);
                    Terminus.enqueueBus(bus);
                }    
            }
                        
            // Some passenger requested to stop here
            else if(bus.getClosestBusStop() == bus.getNextBusStop()) {
                bus.setState(Bus.State.BUSSTOP);
                bus.setPosition(closestBusStop.getPosition());
                BusStop.enqueueBus(bus, closestBusStop);
            }
            
            // If no one wanted to stop here maybe someone is waiting ...
            else {
                // It blocks!
                if(queryForWaitingPassengers(bus.getClosestBusStop())) {
                    bus.setState(Bus.State.BUSSTOP);
                    bus.setPosition(closestBusStop.getPosition());
                    BusStop.enqueueBus(bus, closestBusStop);
                }
                // If noone is waiting, ommit bus stop
                else {
                    bus.setPosition(bus.getPosition() + busSpeed);
                }
            }
        }
        else {
            bus.setPosition(bus.getPosition() + busSpeed);
        }
    }
    
    /**
     * Queries for number of waiting passengers at given bus stop.
     * 
     * @param busStop bus stop to be queried
     * @return true if at least one passenger is waiting
     */
    private static boolean queryForWaitingPassengers(BusStop busStop) {
           
        Simulation.client.send(new WaitingPassengersRequestEvent(
                Simulation.simulationModuleConfig.getModuleName(), 
                busStop.getId()));
        
        return waitForWaitingPassengersQueryResult() > 0;
   
    }
    
    /**
     * Blocks until result of waiting passengers query becomes available.
     * 
     * @return number of waiting passengers.
     */
    private static int waitForWaitingPassengersQueryResult() {
        
        int waitingPassengers = -1;
        while(waitingPassengers < 0) {
            
            for(SimulationEvent event : Simulation.eventQueue) {
                if(event instanceof WaitingPassengersEvent) {
                    waitingPassengers = ((WaitingPassengersEvent)event).getWaitingPassengers();
                    Simulation.eventQueue.remove(event);
                    break;
                }
            }
            
        }
        
        return waitingPassengers;
        
    }
    
    /**
     * Withdraws bus of given id.
     * Bus will be withdrawn to the depot at the moment of the arrival to the 
     * terminus.
     * 
     * @param busId bus id
     */
    public static void withdrawBus(int busId) {
        Bus bus = getBusById(busId);
        if(bus != null) {
            bus.setGoToDepot(true);
        }
        else {
            Simulation.logger.log(Level.WARNING, "No such bus {0}", busId);
        }
    }
    
    /**
     * Checks if 
     */    
    
    /**
     * Sets bus to start at next iteration after arriving from the depot.
     * It clears bus cycles count.
     * 
     * @param busId id of the bus
     */
    public static void sendFromDepot(int busId) {
        Bus bus = getBusById(busId);
        if(bus != null) {
            bus.setState(State.RUNNING);
            bus.cycle = 0;
            //bus.setStartAt(Simulation.getCurrentCycle()+1);
        }
        else {
            Simulation.logger.log(Level.WARNING, "No such bus {0}", busId);
        }
    }
    
    /**
     * Departs bus of given id.
     * 
     * @param busId bus id
     */
    public static void departBus(int busId) {
        Bus bus = getBusById(busId);
        if(bus != null) {
            bus.depart();
        }
        else {
            Simulation.logger.log(Level.WARNING, "No such bus {0}", busId);
        }
    }

    
}
