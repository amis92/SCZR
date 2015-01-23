package burtis.modules.gui.controller;

import java.io.IOException;

import burtis.common.events.EventSender;
import burtis.common.events.Passengers.NewPassengerEvent;
import burtis.common.events.Passengers.PassengerGenerationRateConfigurationEvent;
import burtis.common.events.flow.SetCycleLengthEvent;
import burtis.modules.gui.events.CreatePassengerEvent;
import burtis.modules.gui.events.PassengerGenRateEvent;
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

    public void createPassenger(CreatePassengerEvent e)
    {
        logger.info("Sent new passenger.");
        clientModule.send(new NewPassengerEvent(sender, e.getOrigin(), e
                .getDestination()));
    }

    public void setPassengerGenRates(PassengerGenRateEvent e)
    {
        logger.info("Sent new passenger gen rates.");
        clientModule.send(new PassengerGenerationRateConfigurationEvent(e
                .getGcl(), e.getPpc(), sender));
    }

    public void setCycleLength(long newCycleLength)
    {
        logger.info("Sending new cycle length: " + newCycleLength);
        clientModule.send(new SetCycleLengthEvent(sender, newCycleLength));
    }
}
