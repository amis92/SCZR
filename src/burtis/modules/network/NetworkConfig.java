package burtis.modules.network;

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
        return moduleConfigs;
    }

    public String getServerAddress()
    {
        return serverAddress;
    }
}
