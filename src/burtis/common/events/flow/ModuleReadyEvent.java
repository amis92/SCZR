package burtis.common.events.flow;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;

/**
 * Event to be sent when sender module is ready to start simulation.
 * 
 * @author Mikołaj Sowiński
 */
public class ModuleReadyEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;

    public ModuleReadyEvent(String sender)
    {
        super(sender);
    }

    public ModuleReadyEvent(String sender, String[] recipients)
    {
        super(sender, recipients);
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
