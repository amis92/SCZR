package burtis.modules.passengers.model;

import java.util.LinkedList;
import java.util.List;

import burtis.modules.passengers.Managers;

/**
 * 
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
    
    public TransactionManager(Managers managers) {
        
        this.managers = managers;
        this.managers.setTransactionManager(this);
        
    }
    
}
