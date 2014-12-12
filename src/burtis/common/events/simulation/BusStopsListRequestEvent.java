package burtis.common.events.simulation;

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

    public BusStopsListRequestEvent(String sender)
    {
        super(sender);
    }

    public BusStopsListRequestEvent()
    {
        super(NetworkConfig.defaultConfig().getModuleConfigs()
                .get(NetworkConfig.PSNGR_MODULE).getModuleName(),
                new String[] { NetworkConfig.defaultConfig().getModuleConfigs()
                        .get(NetworkConfig.SIM_MODULE).getModuleName() });
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
