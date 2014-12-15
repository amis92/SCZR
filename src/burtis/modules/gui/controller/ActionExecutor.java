package burtis.modules.gui.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import burtis.common.events.flow.DoStepEvent;
import burtis.common.events.flow.PauseSimulationEvent;
import burtis.common.events.flow.StartSimulationEvent;
import burtis.common.events.flow.TerminateSimulationEvent;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.client.ClientModule;

public class ActionExecutor
{
    private final static Logger logger = Logger.getLogger(ActionExecutor.class
            .getName());
    private final ClientModule clientModule;
    private final String sender;
    private final List<ModuleConfig> modules;
    private final String[] flowEventsRecipients;

    public ActionExecutor(ClientModule clientModule, NetworkConfig netConfig)
    {
        this.clientModule = clientModule;
        this.sender = clientModule.getModuleName();
        this.modules = netConfig.getModuleConfigs();
        this.flowEventsRecipients = new String[] { modules.get(
                NetworkConfig.SYNC_MODULE).getModuleName() };
    }

    public void sendStartEvent()
    {
        clientModule
                .send(new StartSimulationEvent(sender, flowEventsRecipients));
        logger.info("Sent Start");
    }

    public void sendPauseEvent()
    {
        clientModule
                .send(new PauseSimulationEvent(sender, flowEventsRecipients));
        logger.info("Sent Pause");
    }

    public void sendStopEvent()
    {
        clientModule.send(new TerminateSimulationEvent(sender));
        logger.info("Sent Terminate");
    }

    public void sendOneStepEvent()
    {
        clientModule.send(new DoStepEvent(sender, flowEventsRecipients));
        logger.info("Sent DoStep");
    }

    public void connect() throws IOException
    {
        logger.info("Connecting...");
        clientModule.connect();
    }

    public void disconnect()
    {
        clientModule.close();
        logger.info("Connection closed.");
    }
}
