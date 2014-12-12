package burtis.common.events.passengers;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;

/**
 * Superclass for all passenger info requests that are sent by a simulation
 * module during its iteration.
 * 
 * @author Mikołaj Sowiński
 */
public class PassengerInfoRequestEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;

    public PassengerInfoRequestEvent(String sender)
    {
        super(sender);
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
