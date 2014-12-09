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
public class ChangeSimulationSpeedEvent extends SimulationEvent {

    private final long newSimulationSpeed;
    
    public ChangeSimulationSpeedEvent(String sender, long speed) {
        super(sender);
        newSimulationSpeed = speed;
    }
    
    public long speed() { 
        return newSimulationSpeed;
    }
}
