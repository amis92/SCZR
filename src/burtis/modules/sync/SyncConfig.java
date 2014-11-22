package burtis.modules.sync;

import java.util.ArrayList;
import java.util.List;

import burtis.modules.common.ModuleConfig;

public class SyncConfig
{
    
    /**
     * Synchronization module representative name.
     */
    private final String name;
    
    /**
     * Synchronization module port.
     */
    private final int syncPort;
   
    private final List<ModuleConfig> modules;
    
    /*
     * Desired time of single iteration, given in ms.
     */
    private long iterationTime;
    
    /*
     * Timeout for response from modules. Given in ms.
     */
    private int moduleResponseTimeout;

    public SyncConfig(String name, int port, final List<ModuleConfig> modulesList, int iterationTime) {
        this.name = name;
        syncPort = port;
        modules = modulesList;
        this.iterationTime = iterationTime;
    }
    
    public List<ModuleConfig> getModuleConfigs()
    {
        return modules;
    }
    
    public long getIterationTime() { return iterationTime; }
    public String getName() { return name; }
    public int getPort() { return syncPort; }
    public void setIterationTime(long time) { iterationTime = time; } 
    
    public int getModuleResponseTimeout() { return moduleResponseTimeout; }
    
    public List<burtis.modules.network.ModuleConfig> getNetworkServerModuleConfig() {
        List<burtis.modules.network.ModuleConfig> netconfig = new ArrayList<burtis.modules.network.ModuleConfig>();
        List<String> syncConnections = new ArrayList<>();
        
        for(ModuleConfig config : modules) {
            netconfig.add(config);
            syncConnections.add(config.getModuleName());
        }
        
        netconfig.add(new ModuleConfig(name, syncPort, syncConnections.toArray(new String[0]), false));
        
        return netconfig;
    }
    
    public static SyncConfig defaultConfig() {
        
        String guiName = "GUI Module";
        String simName = "Simulation Module";
        String psngrName = "Passengers Module";
        String syncName = "Synchronization Module";
        int syncPort = 8126;
        
        List<ModuleConfig> configs = new ArrayList<>(3);
        configs.add(new ModuleConfig(guiName, 8123, new String[]{psngrName,simName,syncName}, false));
        configs.add(new ModuleConfig(simName, 8124, new String[]{psngrName,guiName,syncName}, true));
        configs.add(new ModuleConfig(psngrName, 8125, new String[]{psngrName, guiName,syncName}, false));
                
        return new SyncConfig(syncName, syncPort, configs, 100);
        
    }
    
    
    
    
}
