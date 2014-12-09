/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.modules.network;

import burtis.modules.network.server.Server;

/**
 *
 * @author Mikołaj
 */
public class CommunicationServer {
    
    
    public static void main(String[] args) {
        Server server = new Server(NetworkConfig.defaultConfig());
        server.run();
    }
    
}
