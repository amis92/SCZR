package burtis.common.events;

/**
 * Superclass for all configuration events.
 * 
 * @author Mikołaj Sowiński
 */
public class ConfigurationEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;

    public ConfigurationEvent(String sender)
    {
        super(sender);
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
