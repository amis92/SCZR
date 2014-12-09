package burtis.common.events;

/**
 *
 * @author Mikołaj Sowiński
 */
public class SendBusEvent extends SimulationEvent {

    private final int busId;
    
    public SendBusEvent(String sender, String[] recipients, int busId) {
        super(sender, recipients);
        this.busId = busId;
    }
    
    public int busId() { 
        return busId;
    }


    public void visit(AbstractEventProcessor eventProcessor)
    {
        eventProcessor.process(this);
    }
}
