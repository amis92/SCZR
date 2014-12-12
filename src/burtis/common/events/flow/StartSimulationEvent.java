package burtis.common.events.flow;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;

public class StartSimulationEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;

    public StartSimulationEvent(String sender, String[] recipients)
    {
        super(sender, recipients);
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
