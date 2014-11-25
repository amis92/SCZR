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
public class DeathErrorEvent extends SimulationEvent {

    private String message;

    /**
     * Get the value of message
     *
     * @return the value of message
     */
    public String getMessage() {
        return message;
    }
    
    public DeathErrorEvent(String sender, String[] recipients, String message) {
        super(sender, recipients);
        this.message = message;
    }
    
}
