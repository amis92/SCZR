package burtis.common.events;

import java.util.List;
import java.util.logging.Logger;

import burtis.common.events.flow.CycleCompletedEvent;
import burtis.common.events.flow.DoStepEvent;
import burtis.common.events.flow.ModuleReadyEvent;
import burtis.common.events.flow.PauseSimulationEvent;
import burtis.common.events.flow.StartSimulationEvent;
import burtis.common.events.flow.TerminateSimulationEvent;
import burtis.common.events.flow.TickEvent;
import burtis.common.mockups.Mockup;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.client.ClientModule;

/**
 * Base class to simplify sending events, mainly flow events.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class EventSender
{
    protected final Logger logger = Logger.getLogger(this.getClass().getName());
    protected final ClientModule clientModule;
    protected final String sender;
    protected final List<ModuleConfig> modules;

    public EventSender(ClientModule clientModule, NetworkConfig netConfig)
    {
        this.clientModule = clientModule;
        this.sender = clientModule.getModuleName();
        this.modules = netConfig.getModuleConfigs();
    }

    public void sendStartEvent()
    {
        clientModule.send(new StartSimulationEvent(sender));
        logger.info("Sent Start");
    }

    public void sendPauseEvent()
    {
        clientModule.send(new PauseSimulationEvent(sender));
        logger.info("Sent Pause");
    }

    public void sendStopEvent()
    {
        clientModule.send(new TerminateSimulationEvent(sender));
        logger.info("Sent Terminate");
    }

    public void sendOneStepEvent()
    {
        clientModule.send(new DoStepEvent(sender));
        logger.info("Sent DoStep");
    }

    public void sendModuleReadyEvent(long iteration)
    {
        clientModule.send(new ModuleReadyEvent(sender, iteration));
        logger.info("Sent ModuleReadyEvent");
    }

    public void sendCycleCompletedEvent(long iteration)
    {
        clientModule.send(new CycleCompletedEvent(sender, iteration));
        logger.info("Sent CycleCompletedEvent");
    }

    public void sendTickEvent(long iteration)
    {
        clientModule.send(new TickEvent(sender, iteration));
        logger.info("Sent TickEvent");
    }

    public void sendMainMockupEvent(Mockup mockup)
    {
        clientModule.send(new MainMockupEvent(sender, mockup));
        logger.info("Sent MainMockupEvent");
    }
}
