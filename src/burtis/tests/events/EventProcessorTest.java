/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.tests.events;

import burtis.common.events.EventProcessor;
import burtis.common.events.SimulationEvent;
import burtis.common.events.StartSimulationEvent;

/**
 *
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 */
public class EventProcessorTest extends EventProcessor {

    @Override
    public void process(SimulationEvent event) {
        System.out.println("SimulationEvent");
    }
    
    @Override
    public void process(StartSimulationEvent event) {
        System.out.println("StartSimulationEvent");
    }

    public EventProcessorTest() {
        
        SimulationEvent event;
        
        event = new SimulationEvent("TEST");
        process(event);
        event = new StartSimulationEvent("TEST", new String[] {});
        process(event);
        System.out.println(event instanceof StartSimulationEvent);
    }
    
    
    
    public static void main(String[] args) {
        new EventProcessorTest();
    }
    
}
