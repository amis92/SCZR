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
        return moduleConfigs;
    }

    public String getServerAddress()
    {
        return serverAddress;
    }

    public static NetworkConfig defaultConfig()
    {
        String serverAddress = "127.0.0.1";
        List<ModuleConfig> configs = new ArrayList<ModuleConfig>(3);
        String guiName = "GUI Module";
        String simName = "Simulation Module";
        String psngrName = "Passengers Module";
        configs.add(new ModuleConfig(guiName, 8123, new String[]{psngrName}));
        configs.add(new ModuleConfig(psngrName, 8124, new String[]{simName, guiName}));
        configs.add(new ModuleConfig(simName, 8125, new String[]{psngrName}));
        return new NetworkConfig(serverAddress, configs);
    }
}
