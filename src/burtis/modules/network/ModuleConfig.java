package burtis.modules.network;

import java.util.ArrayList;
import java.util.List;

public class ModuleConfig
{
    private final List<String> connectedModuleNames;
    private final String moduleName;
    private final int port;

    public ModuleConfig(final String moduleName, int port,
            String[] connectedModuleNames)
    {
        this.connectedModuleNames = new ArrayList<String>(connectedModuleNames.length);
        for (String name : connectedModuleNames)
        {
            this.connectedModuleNames.add(name);
        }
        this.moduleName = moduleName;
        this.port = port;
    } 

    public ModuleConfig(final String moduleName, int port,
            List<String> connectedModuleNames)
    {
        this.connectedModuleNames = connectedModuleNames;
        this.moduleName = moduleName;
        this.port = port;
    }

    public List<String> getConnectedModuleNames()
    {
        return connectedModuleNames;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public int getPort()
    {
        return port;
    }
}
