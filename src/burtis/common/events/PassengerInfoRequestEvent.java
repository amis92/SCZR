/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events;

/**
 * Superclass for all passenger info requests that are sent by a simulation
 * module during its iteration.
 * 
 * @author Mikołaj Sowiński
 */
public class PassengerInfoRequestEvent extends SimulationEvent {

    public PassengerInfoRequestEvent(String sender) {
        super(sender);
    }
    
}
