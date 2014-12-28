package burtis.modules.passengers.model;

import java.util.ArrayList;
import java.util.List;

import burtis.common.constants.PassengersModuleConsts;
import burtis.modules.passengers.Managers;

/**
 * Passenger manager for Passenger Module.
 * 
 * @author Mikołaj Sowiński
 *
 */
public class PassengerManager
{

    /**
     * List of living passengers.
     */
    private final List<Passenger> passengers = new ArrayList<>();
    
    /**
     * Number of iterations between passengers generations.
     */
    private int generationCycleLength = PassengersModuleConsts.PASSENGER_GENERATION_CYCLE_LENGTH;
    
    /**
     * Number of passengers to be generated on each generation.
     */
    private int passengersPerCycle = PassengersModuleConsts.PASSENGERS_PER_GENERATION_CYCLE;
    
    /**
     * Number of iterations to the next generation.
     * 
     * Zero means moment of generation.
     */
    private int leftToGeneration = generationCycleLength;
    
    /**
     * Reference to the managers container.
     */
    private Managers managers;
    
    /**
     * Constructor.
     * 
     * @param managers
     */
    public PassengerManager(Managers managers) {
        
        this.managers = managers;
        managers.setPassengerManager(this);
        
    }
    
/* ##############################################
 * GETTERS AND SETTERS
 * ########################################### */
    
    public void setGenerationCycleLength(int generationCycleLength) {
        this.generationCycleLength = generationCycleLength;
    }

    public void setPassengersPerCycle(int passengersPerCycle) {
        this.passengersPerCycle = passengersPerCycle;
    }
    
    public void setManagers(Managers managers)
    {
        this.managers = managers;
    }
    
    
/* ##############################################
 * END OF GETTERS AND SETTERS
 * ########################################### */
    
    /**
     * Removes passenger from the list.
     * 
     * <b>BEWARE: Does not remove neither from buses nor from bus stops!</b>
     * 
     * @param passenger passenger to be killed.
     */
    public void killPassenger(Passenger passenger) {
        passengers.remove(passenger);
    }
    
    /**
     * Generates number of passengers (passengersPerCycle) at given 
     * interval (generationCycleLength).
     * 
     * Passengers are given random origin bus stops and random destinations
     * taking into account that destination should be further than origin.
     */
    public void generatePassengers() {
        
        if(leftToGeneration == 0) {
            
            leftToGeneration = generationCycleLength-1;
            
            BusStop busStop;
            Passenger passenger;
            
            for(int i=0; i<passengersPerCycle; i++) {
                
                busStop = managers.getBusStopManager().getRandomBusStop();
                passenger = new Passenger(
                        // Origin
                        busStop, 
                        // Destination
                        managers.getBusStopManager().getRandomNextBusStop(busStop));
                busStop.enqueuePassenger(passenger);
                passengers.add(passenger);
            }
        }
        else {
            leftToGeneration--;
        }
    }

    
}
