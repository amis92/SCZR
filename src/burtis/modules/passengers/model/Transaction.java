package burtis.modules.passengers.model;

import java.util.LinkedList;
import java.util.List;

import burtis.common.constants.PassengersModuleConsts;
import burtis.common.constants.SimulationModuleConsts;
import burtis.modules.passengers.Managers;

/**
 * Representation of transaction between bus stop and bus.
 * 
 * @author Mikołaj Sowiński
 */
public class Transaction 
{
    /**
     * Number of iterations left to the end of transactions.
     */
    private int iterations;
    
    /**
     * Bus involved.
     */
    private final Bus bus;
    
    /**
     * Bus stop involved.
     */
    private final BusStop busStop;
    
    /**
     * Reference to the managers.
     */
    private Managers managers;

    /**
     * Constructor.
     * 
     * Calculates transaction time summing passengers that are to be
     * loaded and unloaded and multiplying by constant 
     * {@link PassengersModuleConsts#ITER_PER_PASSENGER}.
     * 
     * @param bus bus involved with the transaction
     * @param busStop bus stop involved with the transaction
     */
    public Transaction(Bus bus, BusStop busStop, Managers managers) 
    {
        this.bus = bus;
        this.busStop = busStop;
        this.managers = managers;
        
        int unloadedPassengers = 0;
        for(Passenger passenger : bus.getPassengers()) {
            if(passenger.getDestination() == busStop) {
                managers.getPassengerManager().killPassenger(passenger);
                bus.getPassengers().remove(passenger);
                unloadedPassengers++;
            }
        }
        
        int loadedPassengers = 0;
        while(bus.getFreePlaces() != 0) {
            bus.getPassengers().add(busStop.getNextPassenger());
            loadedPassengers++;            
        }
        
        this.iterations = (unloadedPassengers + loadedPassengers)*
                Math.round(PassengersModuleConsts.ITER_PER_PASSENGER);
    }

/* ##############################################
 * GETTERS AND SETTERS
 * ########################################### */
    
    public Bus getBus() {
        return bus;
    }

    public BusStop getBusStop() {
        return busStop;
    }
    
/* ##############################################
 * END OF GETTERS AND SETTERS
 * ########################################### */
    
    /**
     * Decreases number of iterations left to the end of transaction.
     */
    public void nextIteration() {
        iterations--;
    }
    
    /**
     * Returns true if transaction is finished.
     * 
     * @return status of the transaction
     */
    public boolean isFinished() {
        return iterations <= 0;
    }
           
}
