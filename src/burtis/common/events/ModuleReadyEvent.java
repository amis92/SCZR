package burtis.common.events;

/**
 * Event to be sent when sender module is ready to start simulation.
 * 
 * @author Mikołaj Sowiński
 */
public class ModuleReadyEvent extends SimulationEvent {

    public ModuleReadyEvent(String sender) {
        super(sender);
    }


    public void visit(AbstractEventProcessor eventProcessor)
    {
        eventProcessor.process(this);
    }
}
