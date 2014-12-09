package burtis.tests.modules.sync;

import burtis.modules.network.NetworkConfig;
import burtis.modules.network.client.ClientModule;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SchedulerModuleTest
{
    
    ClientModule client = new ClientModule(NetworkConfig.defaultConfig().getModuleConfigs().get(NetworkConfig.GUI_MODULE));
    
    private void start() {
        try {
            
            client.connect();
            client.send(null);
            
        } catch (IOException ex) {
            Logger.getLogger(SchedulerModuleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void main(String[] args) {
        
    }
}
