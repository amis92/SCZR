package burtis.modules.passengers;

import burtis.common.constants.PassengersModuleConsts;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of single passenger. 
 * 
 * @author Mikołaj Sowiński
 */
public class Passenger {
    
    private static final List<Passenger> passengers = new ArrayList<>();
    
    /**
     * Passenger id.
     */
    private final int id;
    
    /**
     * Passenger origin.
     * Bus stop where passenger was generated.
     */
    private final BusStop origin;
    /**
     * Passenger destination.
     */
    private final BusStop destination;
    
    /**
     * Time from generation to embark or time from generation.
     */
    private long waitingTime;
    
    /**
     * Bus assigned to the passenger.
     */
    private Bus bus;
    
    
    
    /**
     * New id to be assigned to next generated passenger.
     */
    private static int newId = 0;

    public Passenger(BusStop origin, BusStop destination) {
        this.id = newId++;
        this.origin = origin;
        this.destination = destination;
    }

    // ###############################
    // Getters
    // ###############################
    
    public int getId() {
        return id;
    }

    public BusStop getOrigin() {
        return origin;
    }

    public BusStop getDestination() {
        return destination;
    }

    public long getWaitingTime() {
        return waitingTime;
    }
    
    // ###############################
    // End of getters.
    // ###############################
    
    private static int generationCycleLength = PassengersModuleConsts.PASSENGER_GENERATION_CYCLE_LENGTH;
    private static int passengersPerCycle = PassengersModuleConsts.PASSENGERS_PER_GENERATION_CYCLE;
    private static int generationCycle = generationCycleLength;
    
    public static void setGenerationCycleLength(int generationCycle) {
        Passenger.generationCycleLength = generationCycle;
    }

    public static void setPassengersPerCycle(int passengersPerCycle) {
        Passenger.passengersPerCycle = passengersPerCycle;
    }
    
    public static void deletePassenger(Passenger passenger) {
        passengers.remove(passenger);
    }
          
    public static void generatePassengers() {
        if(generationCycle == 0) {
            generationCycle = generationCycleLength;
            BusStop busStop;
            Passenger passenger;
            for(int i=0; i<passengersPerCycle; i++) {
                busStop = BusStop.getRandomBusStop();
                passenger = new Passenger(busStop, BusStop.getRandomNextBusStop(busStop));
                busStop.enqueuePassenger(passenger);
                passengers.add(passenger);
            }
        }
        else {
            generationCycle--;
        }
    }
    
    @Override
    public String toString() {
        return "Passenger{" + "id=" + id + ", origin=" + origin + ", destination=" + destination + ", waitingTime=" + waitingTime + ", bus=" + bus + '}';
    }    
        
}
