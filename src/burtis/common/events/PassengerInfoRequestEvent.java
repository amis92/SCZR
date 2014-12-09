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


    public void visit(AbstractEventProcessor eventProcessor)
    {
        eventProcessor.process(this);
    }
}
