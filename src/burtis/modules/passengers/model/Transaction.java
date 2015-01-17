package burtis.modules.passengers.model;

import burtis.common.constants.PassengersModuleConsts;
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
    // private Managers managers;
    /**
     * Constructor.
     * 
     * Calculates transaction time summing passengers that are to be loaded and
     * unloaded and multiplying by constant
     * {@link PassengersModuleConsts#ITER_PER_PASSENGER}.
     * 
     * @param bus
     *            bus involved with the transaction
     * @param busStop
     *            bus stop involved with the transaction
     */
    public Transaction(Bus bus, BusStop busStop, Managers managers)
    {
        this.bus = bus;
        this.busStop = busStop;
        // this.managers = managers;
        
        // Processing passengers in the bus
        int unloadedPassengers = 0;
        for (Passenger passenger : bus.getPassengers())
        {
            if (passenger.getDestination() == busStop)
            {
                // If this is passenger's destination - bye!
                bus.getPassengers().remove(passenger);
                managers.getPassengerManager().killPassenger(passenger);
                
                unloadedPassengers++;
            }
        }
        
        // Processing passengers at the bus stop
        int loadedPassengers = 0;
        while (bus.getFreePlaces() != 0)
        {
            Passenger passengerToAdd = busStop.getNextPassenger();
            if(passengerToAdd == null) break;
            
            passengerToAdd.setBus(bus);
            bus.getPassengers().add(passengerToAdd);
            loadedPassengers++;
        }
        this.iterations = (unloadedPassengers + loadedPassengers)
                * Math.round(PassengersModuleConsts.ITER_PER_PASSENGER);
    }

    /*
     * ############################################## 
     * GETTERS AND SETTERS
     * ###########################################
     */
    public Bus getBus()
    {
        return bus;
    }

    public BusStop getBusStop()
    {
        return busStop;
    }

    /*
     * ############################################## 
     * END OF GETTERS AND SETTERS
     * ###########################################
     */
    /**
     * Decreases number of iterations left to the end of transaction.
     */
    public void nextIteration()
    {
        iterations--;
    }

    /**
     * Returns true if transaction is finished.
     * 
     * @return status of the transaction
     */
    public boolean isFinished()
    {
        return iterations <= 0;
    }
    
    /**
     * Returns string describing transaction.
     */
    @Override
    public String toString() {
        return "Bus " + bus.getId() + " at " + busStop.getName() + " left " + iterations;
    }
}
