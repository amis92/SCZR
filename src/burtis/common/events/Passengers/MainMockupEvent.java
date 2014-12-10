/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events.Passengers;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.SimulationEvent;
import burtis.common.mockups.Mockup;

/**
 *
 * @author Mikołaj
 */
public class MainMockupEvent extends SimulationEvent {

    private final Mockup mainMockup;
    
    public MainMockupEvent(String sender, Mockup mainMockup) {
        super(sender);
        this.mainMockup = mainMockup;
    }

    public Mockup getMainMockup() {
        return mainMockup;
    }
    
    

    @Override
    public void visit(AbstractEventProcessor eventProcessor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
