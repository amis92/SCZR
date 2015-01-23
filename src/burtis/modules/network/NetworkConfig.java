package burtis.modules.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Describes whole configuration of burtis network. Contains module descriptions
 * and the address of server.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class NetworkConfig
{
    private final List<ModuleConfig> moduleConfigs;
    private final String serverAddress;

    public NetworkConfig(final String serverAddress,
            final List<ModuleConfig> moduleConfigs)
    {
        this.moduleConfigs = moduleConfigs;
        this.serverAddress = serverAddress;
    }

    public List<ModuleConfig> getModuleConfigs()
    {
        return new ArrayList<ModuleConfig>(moduleConfigs);
    }

    public String getServerAddress()
    {
        return serverAddress;
    }

    /**
     * Builds and returns new default config used in default single-computer
     * environment where all connections are local, and server is available on
     * localhost address 127.0.0.1.
     * 
     * @return The default burtis network description.
     */
    public static NetworkConfig defaultConfig()
    {
        String serverAddress = "127.0.0.1";
        /* 0 */String guiName = "GUI Module";
        /* 1 */String syncName = "Synchronization Module";
        /* 2 */String busName = "Bus Scheduling Module";
        /* 3 */String psgrName = "Passengers Module";
        /* 4 */String simName = "Simulation Module";
        Map<String, Object> syncOptions = new HashMap<>();
        syncOptions.put("iterationTime", 100);
        syncOptions.put("moduleResponseTimeout", 1000);
        List<ModuleConfig> configs = new ArrayList<ModuleConfig>(5);
        configs.add(new ModuleConfig(guiName, serverAddress, 8121, false, null));
        configs.add(new ModuleConfig(syncName, serverAddress, 8122, true,
                syncOptions));
        configs.add(new ModuleConfig(busName, serverAddress, 8123, false, null));
        configs.add(new ModuleConfig(psgrName, serverAddress, 8124, false, null));
        configs.add(new ModuleConfig(simName, serverAddress, 8125, true, null));
        return new NetworkConfig(serverAddress, configs);
    }

    public static String getModuleName(int moduleId)
    {
        return NetworkConfig.defaultConfig().getModuleConfigs().get(moduleId)
                .getModuleName();
    }

    public final static int GUI_MODULE = 0;
    public final static int SYNC_MODULE = 1;
    public final static int BUSSHED_MODULE = 2;
    public final static int PSNGR_MODULE = 3;
    public final static int SIM_MODULE = 4;
}
