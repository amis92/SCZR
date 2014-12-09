/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events;

import java.util.List;

/**
 *
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 */
public class PassengerTransactionEvent extends SimulationEvent {
    
    private final List<PassengerTransaction> transactions;

    /**
     * Get the value of transactions
     *
     * @return the value of transactions
     */
    public List<PassengerTransaction> getTransactions() {
        return transactions;
    }

    
    public PassengerTransactionEvent(String sender, String[] recipients, List<PassengerTransaction> transactions) {
        super(sender, recipients);
        this.transactions = transactions;
    }
    
}
