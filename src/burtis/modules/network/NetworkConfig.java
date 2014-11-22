package burtis.modules.network;

import java.util.ArrayList;
import java.util.List;

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

    public static NetworkConfig defaultConfig()
    {
        String serverAddress = "127.0.0.1";
        List<ModuleConfig> configs = new ArrayList<ModuleConfig>(5);
        String guiName  = "GUI Module";
        String syncName = "Synchronization Module";
        String busName  = "Bus Scheduling Module";
        String psgrName = "Passengers Module";
        String simName  = "Simulation Module";
        configs.add(new ModuleConfig(guiName,   serverAddress, 8121));
        configs.add(new ModuleConfig(syncName,  serverAddress, 8122));
        configs.add(new ModuleConfig(busName,   serverAddress, 8123));
        configs.add(new ModuleConfig(psgrName,  serverAddress, 8124));
        configs.add(new ModuleConfig(simName,   serverAddress, 8125));
        return new NetworkConfig(serverAddress, configs);
    }
}
