/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events.ZTM;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.SimulationEvent;

/**
 *
 * @author Mikołaj
 */
public class ChangeReleasingFrequencyEvent extends SimulationEvent {

    private long newReleasingFrequency;
    
    public ChangeReleasingFrequencyEvent(String sender, String[] recipients, long newReleasingFrequency) {
        super(sender, recipients);
    }

    public long getNewReleasingFrequency() {
        return newReleasingFrequency;
    }
    
    

    @Override
    public void visit(AbstractEventProcessor eventProcessor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
