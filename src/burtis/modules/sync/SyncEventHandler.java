/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.modules.sync;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.CycleCompletedEvent;
import burtis.common.events.SimulationEvent;
import burtis.common.events.TerminateSimulationEvent;
import burtis.common.events.gui.DoStepEvent;
import burtis.common.events.gui.PauseSimulationEvent;
import burtis.common.events.gui.StartSimulationEvent;
import java.util.logging.Level;

/**
 *
 * @author Mikołaj
 */
public class SyncEventHandler extends AbstractEventProcessor {
    
    private final SimulationServer sync = SimulationServer.getInstance();

    @Override
    public void defaultHandle(SimulationEvent event) {
        sync.getLogger().log(Level.WARNING, "Unknown event {0}", event.getClass().getSimpleName());
    }
    
    @Override
    public void process(TerminateSimulationEvent event) {
        sync.terminate();
    }
    
    @Override
    public void process(StartSimulationEvent event) {
        sync.startSimulation();    
    }
    
    @Override
    public void process(PauseSimulationEvent event) {
        sync.pauseSimulation();
    }
    
    @Override
    public void process(DoStepEvent event) {
        sync.doStep();
    }
    
    @Override
    public void process(CycleCompletedEvent event) {
        // Being not in sync is uncacceptable.
        if(event.iteration() != sync.getIteration()) {
            sync.getLogger().log(Level.SEVERE, "Synchronization error. "
                    + "Module {0} is not in sync.", event.sender());
            sync.terminate();
        }
        else {
            Module.setReady(event.sender());
            if(Module.checkNextIterationClearance()) sync.readyForTick();
        }
    }

    
}
