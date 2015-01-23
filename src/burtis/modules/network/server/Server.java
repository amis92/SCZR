package burtis.modules.network.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;
import burtis.common.events.flow.TerminateSimulationEvent;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.server.impl.ServerSender;

/**
 * Performs traffic forwarding according to configuration provided. All traffic
 * from given module is forwarded to all recipients of that module, except of
 * {@link ServerOrder}s which are executed and not forwarded. </br></br>
 * 
 * Each connection listens on separate thread for incoming traffic. All outgoing
 * traffic is sent through another thread with blocking queue.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class Server extends AbstractEventHandler
{
    protected final static Logger logger = Logger.getLogger(Server.class
            .getName());
    private final Collection<ModuleConnection> moduleConnections;
    private final Map<String, ModuleConnection> moduleMap = new HashMap<>();
    private final ServerSender sender = new ServerSender();
    protected boolean isRunning = false;

    public Server(final NetworkConfig netConfig)
    {
        final Collection<ModuleConfig> configs = netConfig.getModuleConfigs();
        this.moduleConnections = new ArrayList<>(configs.size());
        final ModuleConnectionFactory factory = new ModuleConnectionFactory(
                moduleConnections, moduleMap, this::receive);
        factory.readConfig(netConfig);
    }

    @Override
    public void defaultHandle(SimulationEvent event)
    {
        if (!event.getRecipients().isEmpty())
        {
            for (String recipientName : event.getRecipients())
            {
                forward(event, recipientName);
            }
        }
        else
        {
            String senderName = event.sender();
            for (ModuleConnection moduleConnection : moduleConnections)
            {
                if (moduleConnection.getModuleName().equalsIgnoreCase(
                        senderName))
                {
                    continue;
                }
                forward(event, moduleConnection.getModuleName());
            }
        }
    }

    @Override
    public void process(TerminateSimulationEvent event)
    {
        String senderName = event.sender();
        for (ModuleConnection moduleConnection : moduleConnections)
        {
            if (moduleConnection.getModuleName().equalsIgnoreCase(senderName))
            {
                continue;
            }
            sender.send(event, moduleConnection);
        }
    }

    public void run()
    {
        logger.log(Level.INFO, "Server preparing to run");
        sender.startSending();
        for (ModuleConnection moduleConnection : moduleConnections)
        {
            moduleConnection.connect();
        }
        isRunning = true;
        logger.log(Level.INFO, "Server running");
    }

    public void stop()
    {
        if (!isRunning)
        {
            return;
        }
        isRunning = false;
        logger.log(Level.INFO, "Server stopping...");
        for (ModuleConnection moduleConnection : moduleConnections)
        {
            moduleConnection.close();
        }
        sender.stopSending();
        logger.log(Level.INFO, "Server stopped");
    }

    private void forward(SimulationEvent event, String recipientName)
    {
        if (moduleMap.containsKey(recipientName))
        {
            sender.send(event, moduleMap.get(recipientName));
        }
        else
        {
            logger.warning("No such module - can't send object to "
                    + recipientName);
        }
    }

    private void receive(Object receivedObject)
    {
        if (receivedObject instanceof SimulationEvent)
        {
            SimulationEvent event = (SimulationEvent) receivedObject;
            logger.finer("Forwarding object " + event.getClass().getName());
            event.visit(this);
        }
        else
        {
            logger.warning("Received unexpected object: "
                    + receivedObject.getClass().getName());
        }
    }
}
