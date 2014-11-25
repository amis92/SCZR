/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events;

/**
 *
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 */
public class PassengerTransactionEvent extends SimulationEvent {

    public PassengerTransactionEvent(String sender, String[] recipients) {
        super(sender, recipients);
    }
    
}
