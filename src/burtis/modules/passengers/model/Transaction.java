package burtis.modules.passengers.model;

import java.util.List;

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
     * Number of iterations left to the end of transaction.
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
     * Loads and unloads passengers. Calculates transaction time summing
     * passengers that are loaded and unloaded and multiplying by constant
     * {@link PassengersModuleConsts#ITER_PER_PASSENGER}.
     * 
     * @param bus
     *            - bus involved with the transaction.
     * @param busStop
     *            - bus stop involved with the transaction.
     * @param managers
     *            - managers container.
     */
    public Transaction(Bus bus, BusStop busStop, Managers managers)
    {
        this.bus = bus;
        this.busStop = busStop;
        // Processing passengers in the bus
        int unloadedPassengers = 0;
        List<Passenger> passengers = bus.getPassengers();
        for (int i = 0; i < passengers.size(); i++)
        {
            if (passengers.get(i).getDestination() == busStop)
            {
                // If this is passenger's destination - bye!
                managers.getPassengerManager().killPassenger(passengers.get(i));
                bus.getPassengers().remove(i);
                i--;
                unloadedPassengers++;
            }
        }
        // Processing passengers at the bus stop
        int loadedPassengers = 0;
        while (bus.getFreePlaces() != 0)
        {
            Passenger passengerToAdd = busStop.getNextPassenger();
            if (passengerToAdd == null)
                break;
            passengerToAdd.setBus(bus);
            bus.getPassengers().add(passengerToAdd);
            loadedPassengers++;
        }
        this.iterations = (int) Math
                .ceil((unloadedPassengers + loadedPassengers)
                        * (PassengersModuleConsts.ITER_PER_PASSENGER));
    }

    /*
     * ############################################## GETTERS AND SETTERS
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
     * ############################################## END OF GETTERS AND SETTERS
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
    public String toString()
    {
        return "Bus " + bus.getId() + " at " + busStop.getName() + " left "
                + iterations;
    }
}
