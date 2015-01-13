package burtis.common.events;

import burtis.common.mockups.Mockup;
import burtis.modules.network.NetworkConfig;

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
        super(sender, new String[] {NetworkConfig.getModuleName(NetworkConfig.GUI_MODULE)});
        this.mainMockup = mainMockup;
    }

    public MainMockupEvent(String sender, String[] recipients, Mockup mainMockup)
    {
        super(sender, recipients);
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
