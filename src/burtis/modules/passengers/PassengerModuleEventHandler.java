/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.modules.passengers;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.MainMockupEvent;
import burtis.common.mockups.MockupBus;
import burtis.common.events.SimulationEvent;
import burtis.common.events.flow.CycleCompletedEvent;
import burtis.common.events.flow.TerminateSimulationEvent;
import burtis.common.events.flow.TickEvent;
import burtis.common.events.passengers.PassengerGenerationRateConfigurationEvent;
import burtis.common.events.passengers.PassengerInfoRequestEvent;
import burtis.common.events.passengers.WaitingPassengersEvent;
import burtis.common.events.passengers.WaitingPassengersRequestEvent;
import burtis.common.events.simulation.BusArrivesAtBusStopEvent;
import burtis.common.events.simulation.BusMockupsEvent;
import burtis.common.events.simulation.BusStopsListEvent;
import burtis.common.mockups.Mockup;
import burtis.modules.network.NetworkConfig;

import java.util.logging.Level;

/**
 *
 * @author Mikołaj
 */
public class PassengerModuleEventHandler extends AbstractEventHandler {
    
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
    public void process(WaitingPassengersRequestEvent event) {
        
        // Only in RUNNING state!
        if(pm.state == PassengerModule.State.INIT) return;
        
        int busStopId = ((WaitingPassengersRequestEvent)event).getBusStopId();
        int waitingPassengers = BusStop.waitingPassengers(busStopId);
        pm.send(new WaitingPassengersEvent(
                pm.getModuleConfig().getModuleName(),
                busStopId,
                waitingPassengers));
        
        
    }
    
    @Override
    public void process(TickEvent event) {
        
        PassengerModule.getInstance().getLogger().log(Level.INFO, "Tick received, iteration {0}", event.iteration());
        
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
                pm.getLogger().log(Level.SEVERE, "Incorrect cycle number(CycleCompletedEvent). Terminating.");
                pm.terminate();
            }
            
            // Check if there are bus mockups available
            if(pm.busMockups == null) {
                pm.getLogger().log(Level.SEVERE, "No bus mockups are available. Terminating.");
                pm.terminate();
            }

            Passenger.generatePassengers();
            Transaction.tickTransactions();
            // Send all event that are to be sent.
            EventBuilder.getEvents(pm.getModuleConfig().getModuleName())
                .forEach((SimulationEvent eventt) -> {
                    pm.send(eventt);
                });
            
            // Send mockup to the gui module
            MainMockupEvent mockupEvent = new MainMockupEvent(
                    pm.getModuleConfig().getModuleName(),
                    PassengerModule.getMockup());
            mockupEvent.getMainMockup().print();
            pm.send(mockupEvent);
            
            pm.sendCycleCompleted();
        
            pm.currentCycle = -1;
            pm.busMockups = null;
            
           
            
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
        //pm.send(new ModuleReadyEvent(pm.getModuleConfig().getModuleName()));
        pm.state = PassengerModule.State.RUNNING;
        BusStop.printBusStopsList();
    }
    
    @Override
    public void process(BusMockupsEvent event) {
        
        if(event.iteration() != pm.currentCycle) {
            pm.getLogger().log(Level.SEVERE, "Incorrect cycle number (BusMockupEvent). Terminating.");
            pm.terminate();
        }
        
        PassengerModule.busMockups = event.getBusMockups();
    }
    
    
}
