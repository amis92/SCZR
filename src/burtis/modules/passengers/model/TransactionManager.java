package burtis.modules.passengers.model;

import java.util.LinkedList;
import java.util.List;

import burtis.modules.passengers.Managers;

/**
 * Transaction manager.
 * 
 * @author Mikołaj Sowiński
 *
 */
public class TransactionManager
{
    /**
     * List of active transactions.
     */
    private final List<Transaction> transactions = new LinkedList<>();

    /**
     * Reference to managers.
     */
    private final Managers managers;
    
    /**
     * Constructor.
     * 
     * @param managers
     */
    public TransactionManager(Managers managers) {
        this.managers = managers;
        this.managers.setTransactionManager(this);       
    }
    
    /**
     * Ticks all transactions, departs buses and removes finished transactions.
     */
    public void tickTransactions() {
        
        for(int i=0; i<transactions.size(); i++) 
        {            
            Transaction transaction = transactions.get(i);
            
            managers.getLogger().info("TSTA: " + transaction);
            
            transaction.nextIteration();
            
            // Removal of finished transactions and departing buses.
            if(transaction.isFinished()) {
                transaction.getBus().depart();
                transaction.getBusStop().departBus();
                transactions.remove(i);
                managers.getLogger().info("TREM: " + transaction);
                i--;
            }
        }  
    }
        
    /**
     * Adds new transaction to the transactions list.
     * 
     * @param transaction transaction to be added
     */
    public void addTransaction(Transaction transaction) {
        managers.getLogger().info("TADD: " + transaction);
        transactions.add(transaction);
    }
    
}
