package burtis.modules.gui.controller;

import java.io.IOException;

import burtis.common.events.EventSender;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.client.ClientModule;

public class ActionExecutor extends EventSender
{
    public ActionExecutor(ClientModule clientModule, NetworkConfig netConfig)
    {
        super(clientModule, netConfig);
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
