package burtis.common.events;

import burtis.common.mockups.Mockup;

/**
 *
 * @author Mikołaj
 */
public class MainMockupEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;
    private final Mockup mainMockup;

    public MainMockupEvent(String sender, Mockup mainMockup)
    {
        super(sender);
        this.mainMockup = mainMockup;
    }

    public Mockup getMainMockup()
    {
        return mainMockup;
    }

    @Override
    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
