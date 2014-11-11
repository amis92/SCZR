package burtis.modules.network.server;

import java.util.function.Consumer;

import order.ServerOrder;
import burtis.modules.network.ModuleConfig;

/**
 * Allows creation of module connection objects from provided configuration
 * objects.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class ModuleConnectionFactory
{
    private final Sender sender;
    private final Consumer<ServerOrder> serverOrderExecutor;

    public ModuleConnectionFactory(final Sender sender,
            final Consumer<ServerOrder> serverOrderExecutor)
    {
        this.sender = sender;
        this.serverOrderExecutor = serverOrderExecutor;
    }

    public ModuleConnection createFromConfig(ModuleConfig moduleConfig)
    {
        final String moduleName = moduleConfig.getModuleName();
        final int port = moduleConfig.getPort();
        final SocketService socketService = new ServerSocketService(port);
        final ModuleConnection connection = new ModuleConnection(moduleName,
                socketService, sender, serverOrderExecutor);
        return connection;
    }
}
