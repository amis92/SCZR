package burtis.common.events;

/**
 *
 * @author Mikołaj Sowiński
 */
public class TerminateSimulationEvent extends SimulationEvent {

    public TerminateSimulationEvent(String sender) {
        super(sender);
    }


    public void visit(AbstractEventProcessor eventProcessor)
    {
        eventProcessor.process(this);
    }
}
