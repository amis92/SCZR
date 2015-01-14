package burtis.common.events.flow;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;
import burtis.modules.network.NetworkConfig;

public class SetCycleLengthEvent extends SimulationEvent
{
    private static final long serialVersionUID = -7989779856296248255L;
    public static final String[] defaultRecipients = new String[] { NetworkConfig
            .getModuleName(NetworkConfig.SYNC_MODULE) };
    private final long cycleLength;

    public SetCycleLengthEvent(String sender, long cycleLength)
    {
        super(sender, defaultRecipients);
        this.cycleLength = cycleLength;
    }

    @Override
    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
    
    public long getCycleLength()
    {
        return cycleLength;
    }
}
