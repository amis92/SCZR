/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events;

/**
 * Event to be send upon module main loop completion.
 * 
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 */
public class CycleCompletedEvent extends SimulationEvent {
    
    private final boolean significantEventHappened;

    public CycleCompletedEvent(String sender, boolean significantEventHappened) {
        super(sender);
        this.significantEventHappened = significantEventHappened;
    }
   
    public boolean significant() {
        return significantEventHappened;
    }
    
}
