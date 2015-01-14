package burtis.common.events.Passengers;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;
import burtis.modules.network.NetworkConfig;

public class NewPassengerEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1816875046828801578L;
    public static final String[] defaultRecipients = new String[] { NetworkConfig
            .getModuleName(NetworkConfig.PSNGR_MODULE) };
    private final String origin;
    private final String destination;

    public NewPassengerEvent(String sender, String origin, String destination)
    {
        super(sender, defaultRecipients);
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }

    public String getOrigin()
    {
        return origin;
    }

    public String getDestination()
    {
        return destination;
    }
}
