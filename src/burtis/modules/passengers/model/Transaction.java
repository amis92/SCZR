package burtis.modules.passengers.model;

import java.util.LinkedList;
import java.util.List;

import burtis.common.constants.PassengersModuleConsts;

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

    private Transaction(int iterations, Bus bus, BusStop busStop) {
        this.iterations = iterations;
        this.bus = bus;
        this.busStop = busStop;
    }

    public Bus getBus() {
        return bus;
    }

    public BusStop getBusStop() {
        return busStop;
    }
    
    public void nextIteration() {
        iterations--;
    }
    
    public boolean isFinished() {
        return iterations <= 0;
    }
    
    /**
     * Creates transaction involving given bus and bus stop.
     * Passengers that are traveling to the given bus stop are removed and new
     * passengers are placed in the bus. It happens at the moment of creation
     * of transaction. Transaction length is computed at the basis of number of
     * passengers being unloaded and loaded.
     * @param bus bus being taking part in transaction
     * @param busStop bus stop taking part in transaction
     */
    public static void newTransaction(Bus bus, BusStop busStop) {
        
        int transactionTime = 0;
        
        // Passengers unloading
        int unloadedPassengers = 0;
        for(Passenger passenger : bus.getPassengers()) {
            if(passenger.getDestination() == busStop) {
                bus.getPassengers().remove(passenger);
                Passenger.killPassenger(passenger);
                unloadedPassengers++;
            }
        }
        
        // Passengers loading
        int loadedPassengers = 0;
        while(bus.getFreePlaces() != 0) {
            bus.getPassengers().add(busStop.getNextPassenger());
            loadedPassengers++;            
        }
        
        transactionTime = (unloadedPassengers + loadedPassengers)*
                Math.round(PassengersModuleConsts.ITER_PER_PASSENGER);
        transactions.add(new Transaction(transactionTime, bus, busStop));     

    }
    
    /**
     * Ticks all transactions, departs buses and removes finished transactions.
     */
    public static void tickTransactions() {
        
        for(Transaction transaction : transactions) {
            transaction.nextIteration();
            
            if(transaction.isFinished()) {
                transaction.getBus().depart();
                transaction.getBusStop().departBus();
            }
        }
        
        Transaction.removeFinishedTransactions(transactions);
        
    }
    
    /**
     * Removes finished transactions.
     * @param transactions list of transactions
     */
    public static void removeFinishedTransactions(final List<Transaction> transactions) {
        
        for(Transaction transaction : transactions) {
            if(transaction.isFinished()) {
                transactions.remove(transaction);
            }
        }
        
    }
    
    
    
}
