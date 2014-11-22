package burtis.modules.network;

public class ModuleConfig
{
    private final String moduleName;
    private final String serverAddress;
    private final int serverPort;
    private final boolean isCritical;

    public ModuleConfig(final String moduleName, final String serverAddress,
            int serverPort, final boolean isCritical)
    {
        this.moduleName = moduleName;
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
        this.isCritical = isCritical;
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

    public boolean isCritical()
    {
        return isCritical;
    }
}
