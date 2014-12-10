/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.modules.passengers;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.CycleCompletedEvent;
import burtis.common.events.ModuleReadyEvent;
import burtis.common.events.PassengerInfoRequestEvent;
import burtis.common.events.Passengers.WaitingPassengersEvent;
import burtis.common.events.Simulation.BusArrivesAtBusStopEvent;
import burtis.common.events.Simulation.BusStopsListEvent;
import burtis.common.events.Simulation.WaitingPassengersRequestEvent;
import burtis.common.events.SimulationEvent;
import burtis.common.events.Sync.TickEvent;
import burtis.common.events.TerminateSimulationEvent;
import burtis.common.events.gui.PassengerGenerationRateConfigurationEvent;
import burtis.modules.network.NetworkConfig;
import java.util.logging.Level;

/**
 *
 * @author Mikołaj
 */
public class PassengerModuleEventHandler extends AbstractEventProcessor {
    
    private final PassengerModule pm = PassengerModule.getInstance();

    @Override
    public void defaultHandle(SimulationEvent event) {
        pm.getLogger().log(Level.WARNING, "Unknown event {0}", event.getClass().getSimpleName());
    }
    
    @Override
    public void process(TerminateSimulationEvent event) {
        pm.terminate();
    }
    
    @Override
    public void process(PassengerGenerationRateConfigurationEvent event) {
        Passenger.setGenerationCycleLength(event.getGenerationCycleLength());
        Passenger.setPassengersPerCycle(event.getPassengersPerCycle());
    }
            
    @Override
    public void process(PassengerInfoRequestEvent event) {
        
        // Only in RUNNING state!
        if(pm.state == PassengerModule.State.INIT) return;
        
        if(event instanceof WaitingPassengersRequestEvent) {
            int busStopId = ((WaitingPassengersRequestEvent)event).getBusStopId();
            int waitingPassengers = BusStop.waitingPassengers(busStopId);
            pm.send(new WaitingPassengersEvent(
                    pm.getModuleConfig().getModuleName(),
                    busStopId,
                    waitingPassengers));
        }
        
        // Unknown event handler.
        else {
            pm.getLogger().log(Level.WARNING, "Unknown event, type: {0}. Event igored.",
                    event.getClass().getSimpleName());
        }
        
    }
    
    @Override
    public void process(TickEvent event) {
        
        // Only in RUNNING state!
        if(pm.state == PassengerModule.State.INIT) return;
        
        // If there is TickEvent before CycleComleted then sth. is VERY wrong.
        if(pm.currentCycle > 0 && event.iteration() != pm.currentCycle) {
            pm.getLogger().log(Level.SEVERE, 
                    "Tick received before completion of cycle. Terminating.");
            pm.terminate();
        }

        pm.currentCycle = event.iteration();
    }
    
    @Override
    public void process(CycleCompletedEvent event) {
        
        // Only in RUNNING state!
        if(pm.state == PassengerModule.State.INIT) return;
        
        // It must originate from simulation module.
        if(event.sender().equals(NetworkConfig.defaultConfig().getModuleConfigs().get(NetworkConfig.SIM_MODULE).getModuleName())) {

            // Check if it corresonds to the right cycle (not -> kill).
            if(event.iteration() != pm.currentCycle) {
                pm.getLogger().log(Level.SEVERE, "Incorrect cycle number. Terminating.");
                pm.terminate();
            }

            Passenger.generatePassengers();
            Transaction.tickTransactions();
            // Send all event that are to be sent.
            EventBuilder.getEvents(pm.getModuleConfig().getModuleName())
                .forEach((SimulationEvent eventt) -> {
                    pm.send(eventt);
                });
        
            pm.currentCycle = -1;
        }
    
        
    }
    
    @Override
    public void process(BusArrivesAtBusStopEvent event) {
        
        // Only in RUNNING state!
        if(pm.state == PassengerModule.State.INIT) return;
        
        Bus bus = Bus.getBus(event.getBusId());
        BusStop busStop = BusStop.getBusStop(event.getBusStopId());
        
        if(busStop == null) {
            pm.getLogger().log(Level.SEVERE, "BusStop@{0} is unknown! Terminating.", event.getBusStopId());
            pm.terminate();
        }
        else {
        
            if(bus == null) {
                bus = Bus.add(event.getBusId());
            }

            bus.arrive();
            busStop.enqueueBus(bus);
        
        }
        
    }
    
    @Override
    public void process(BusStopsListEvent event) {
        pm.getLogger().log(Level.INFO, "Bus stops list received. Entering running state.");
        BusStop.add(event.getBusStops());
        pm.send(new ModuleReadyEvent(pm.getModuleConfig().getModuleName()));
        pm.state = PassengerModule.State.RUNNING;
        BusStop.printBusStopsList();
    }
    
    
}
