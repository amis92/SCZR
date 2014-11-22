/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.modules.sync;

import burtis.modules.network.NetworkConfig;
import burtis.modules.network.server.Server;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 */
public class ReflectorTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SyncConfig config = SyncConfig.defaultConfig();
        Server server = new Server(new NetworkConfig("127.0.0.1", config.getNetworkServerModuleConfig()));
        server.run();
        
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(ReflectorTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            server.stop();
        }
    }
    
}
