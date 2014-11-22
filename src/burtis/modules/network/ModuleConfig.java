package burtis.modules.network;

public class ModuleConfig
{
    private final String moduleName;
    private final String serverAddress;
    private final int serverPort;

    public ModuleConfig(final String moduleName, final String serverAddress,
            int serverPort)
    {
        this.moduleName = moduleName;
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public int getServerPort()
    {
        return serverPort;
    }

    public String getServerAddress()
    {
        return serverAddress;
    }
}
