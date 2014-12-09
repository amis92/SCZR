package burtis.common.events.gui;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.SimulationEvent;

public class StartSimulationEvent extends SimulationEvent
{
    
    private static final long serialVersionUID = 1L;

    public StartSimulationEvent(String sender, String[] recipients) {
        super(sender, recipients);
    }

    public void visit(AbstractEventProcessor eventProcessor)
    {
        eventProcessor.process(this);
    }
}
