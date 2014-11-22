package burtis.modules.network.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import order.ServerOrder;
import order.ServerOrderExecutor;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.server.impl.ServerSender;

/**
 * Performs traffic forwarding according to configuration provided. All traffic
 * from given module is forwarded to all recipients of that module, except of
 * {@link ServerOrder}s which are executed and not forwarded.
 * 
 * Each connection listens on separate thread for incoming traffic. All outgoing
 * traffic is sent through another thread with blocking queue.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class Server implements ServerOrderExecutor
{
    private final static Logger logger = Logger.getLogger(Server.class
            .getName());
    private final ServerSender sendingService = new ServerSender();
    private final Collection<ModuleConnection> moduleConnections;
    private final Map<String, ModuleConnection> moduleMap = new HashMap<>();

    public Server(final NetworkConfig netConfig)
    {
        final Collection<ModuleConfig> configs = netConfig.getModuleConfigs();
        this.moduleConnections = new ArrayList<>(configs.size());
        final ModuleConnectionFactory factory = new ModuleConnectionFactory(
                sendingService, this::forward);
        for (ModuleConfig config : configs)
        {
            ModuleConnection connection = factory.createFromConfig(config);
            moduleConnections.add(connection);
            moduleMap.put(connection.getModuleName(), connection);
        }
    }

    public void run()
    {
        logger.log(Level.INFO, "Server preparing to run");
        sendingService.startSending();
        for (ModuleConnection moduleConnection : moduleConnections)
        {
            moduleConnection.connect();
        }
        logger.log(Level.INFO, "Server running");
    }

    @Override
    public void stop()
    {
        logger.log(Level.INFO, "Server stopping...");
        for (ModuleConnection moduleConnection : moduleConnections)
        {
            moduleConnection.close();
        }
        sendingService.stopSending();
        logger.log(Level.INFO, "Server stopped");
    }

    private void forward(Object receivedObject)
    {
    }
}
