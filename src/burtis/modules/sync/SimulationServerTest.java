/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.modules.sync;

import burtis.common.events.SimulationEvent;
import burtis.common.events.TerminateSimulationEvent;
import burtis.modules.network.client.Client;

/**
 *
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 */
public class SimulationServerTest {
    
    String name = "Test";
    Client<SimulationEvent> client = new Client<>("127.0.0.1", 8124);
    
    public static void main(String[] args)
    {
        SimulationServerTest app = new SimulationServerTest();
        app.client.connect();
        app.testScenario1();
        app.client.closeConnection();
    }
    
    /**
     * TerminateSimulationEvent
     */
    public void testScenario1() {
        client.send(new TerminateSimulationEvent(name));
    }
    
    
}
