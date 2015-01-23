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
    private final Managers managers;

    public TransactionManager(Managers managers)
    {
        this.managers = managers;
        this.managers.setTransactionManager(this);
    }

    /**
     * Ticks all transactions, departs buses and removes finished transactions.
     */
    public void tickAndRemoveTransactions()
    {
        for (int i = 0; i < transactions.size(); i++)
        {
            Transaction transaction = transactions.get(i);
            transaction.nextIteration();
            // Removal of finished transactions and departing buses.
            if (transaction.isFinished())
            {
                transaction.getBus().depart();
                transaction.getBusStop().departBus();
                managers.getBusManager().addToDepartingList(
                        transaction.getBus());
                transactions.remove(i);
                i--;
                managers.getLogger().info("TREM: " + transaction);
            }
        }
    }

    /**
     * Adds new transaction to the transactions list.
     * 
     * @param transaction
     *            transaction to be added
     */
    public void addTransaction(Transaction transaction)
    {
        managers.getLogger().info("TADD: " + transaction);
        transactions.add(transaction);
    }

    public void logListOfTransactions()
    {
        String infoString = "Transactions:\n";
        for (Transaction transaction : transactions)
        {
            infoString += transaction + "\n";
        }
        managers.getLogger().info(infoString + "\n");
    }
}
