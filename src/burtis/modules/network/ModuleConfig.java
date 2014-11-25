package burtis.modules.network;

import java.util.Map;

public class ModuleConfig
{
    
    private final String moduleName;
    private final String serverAddress;
    private final int serverPort;
    private final boolean isCritical;
    private final Map<String,Object> options;

    public ModuleConfig(final String moduleName, final String serverAddress,
            int serverPort, final boolean isCritical, Map<String,Object> options)
    {
        this.moduleName = moduleName;
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
        this.isCritical = isCritical;
        if(options!=null) this.options = options; else this.options = null;
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
    
    public Object getOption(String optionName) {
        return options.get(optionName);
    }

    public boolean isCritical()
    {
        return isCritical;
    }
}
