/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events;

/**
 * Event that is to be sent when iteration time was not satisfied and slow down
 * was done.
 * 
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 */
public class SlowDownEvent extends SimulationEvent {
    
    private final long newIterationTime;

    public SlowDownEvent(String sender, long newIterationTime) {
        super(sender);
        this.newIterationTime = newIterationTime;
    }
    
    public long getNewTime() {
        return newIterationTime;
    }
    
}
