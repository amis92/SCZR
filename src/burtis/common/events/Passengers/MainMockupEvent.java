/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events.Passengers;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.SimulationEvent;

/**
 *
 * @author Mikołaj
 */
public class MainMockupEvent extends SimulationEvent {

    public MainMockupEvent(String sender) {
        super(sender);
    }

    @Override
    public void visit(AbstractEventProcessor eventProcessor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
