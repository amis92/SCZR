package burtis.common.events.flow;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;

/**
 *
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 */
public class PauseSimulationEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;

    public PauseSimulationEvent(String sender, String[] recipients)
    {
        super(sender, recipients);
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
