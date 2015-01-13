package burtis.modules.network.server;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.SocketService;
import burtis.modules.network.server.impl.ServerSocketService;

/**
 * Allows creation of module connection objects from provided configuration
 * objects.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class ModuleConnectionFactory
{
    private final Collection<ModuleConnection> moduleConnections;
    private final Map<String, ModuleConnection> moduleMap;
    private final Consumer<Object> receive;

    public ModuleConnectionFactory(
            final Collection<ModuleConnection> moduleConnections,
            final Map<String, ModuleConnection> moduleMap,
            final Consumer<Object> receive)
    {
        this.moduleConnections = moduleConnections;
        this.moduleMap = moduleMap;
        this.receive = receive;
    }

    public void readConfig(NetworkConfig netConfig)
    {
        for (ModuleConfig config : netConfig.getModuleConfigs())
        {
            ModuleConnection connection = addModuleFromConfig(config);
            moduleConnections.add(connection);
            moduleMap.put(connection.getModuleName(), connection);
        }
    }

    private ModuleConnection addModuleFromConfig(ModuleConfig moduleConfig)
    {
        final String moduleName = moduleConfig.getModuleName();
        final int port = moduleConfig.getServerPort();
        final SocketService socketService = new ServerSocketService(port);
        final ModuleConnection connection = new ModuleConnection(moduleName,
                socketService, receive);
        return connection;
    }
}
