package burtis.common.events.Simulator;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;
import burtis.modules.network.NetworkConfig;

/**
 *
 * @author Mikołaj Sowiński
 */
public class BusStopsListRequestEvent extends SimulationEvent
{
    private static final long serialVersionUID = 1L;
    public static final String[] defaultRecipients = new String[] { NetworkConfig
            .getModuleName(NetworkConfig.SIM_MODULE) };
    public static final String defaultSender = NetworkConfig
            .getModuleName(NetworkConfig.PSNGR_MODULE);

    /**
     * Sends to default recipient.
     * @param sender
     */
    public BusStopsListRequestEvent(String sender)
    {
        super(sender, defaultRecipients);
    }

    /**
     * Creates event with default sender and default recipient.
     */
    public BusStopsListRequestEvent()
    {
        this(defaultSender);
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
