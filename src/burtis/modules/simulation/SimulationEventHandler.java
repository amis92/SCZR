package burtis.modules.simulation;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.Passengers.BusDepartEvent;
import burtis.common.events.Passengers.BusStopsListRequestEvent;
import burtis.common.events.Passengers.WaitingPassengersEvent;
import burtis.common.events.Simulation.BusStopsListEvent;
import burtis.common.events.SimulationEvent;
import burtis.common.events.Sync.TickEvent;
import burtis.common.events.TerminateSimulationEvent;
import burtis.modules.simulation.models.Bus;
import burtis.modules.simulation.models.BusStop;
import burtis.modules.simulation.models.Terminus;
import java.util.logging.Level;

/**
 *
 * @author Mikołaj
 */
public class SimulationEventHandler extends AbstractEventProcessor {
    
    private final Simulation sim = Simulation.getInstance();

    @Override
    public void defaultHandle(SimulationEvent event) {
        Simulation.getInstance().getLogger().log(Level.WARNING, "Unknown event {0}", event.getClass().getSimpleName());
    }
    
    @Override
    public void process(TerminateSimulationEvent event) {
        sim.terminate();
    }
    
    @Override
    public void process(BusStopsListRequestEvent event) {
        Simulation.getInstance().getLogger().log(Level.INFO, "Bus stops list requested from {0}.", event.sender());
        sim.send(new BusStopsListEvent(
            sim.getModuleConfig().getModuleName(),
            new String[] { event.sender() },
            BusStop.getBusStopsList()));
    }
    
    @Override
    public void process(TickEvent event) {
        Simulation.getInstance().getLogger().log(Level.INFO, "Tick received, iteration {0}", event.iteration());
        if(sim.currentCycle > 0 && event.iteration() != sim.currentCycle) {
            Simulation.getInstance().getLogger().log(Level.SEVERE, 
                    "Tick received before completion of cycle. Terminating.");
            sim.terminate();
        }

        sim.currentCycle = event.iteration();
        Terminus.departBus();
        Bus.updatePositions();
        
        

// Send bus state
        // Send cycle completed
    }
    
    @Override
    public void process(BusDepartEvent event) {
        Simulation.getInstance().getLogger().log(Level.INFO, "Bus {0} departs from the bus stop.", event.getBusId());
        Bus bus = Bus.getBusById(event.getBusId());
        if(bus != null) {
            bus.depart(BusStop.getBusStopById(event.getNextBusStopId()));
        }
        else {
            Simulation.getInstance().getLogger().log(Level.SEVERE, "Bus {0} does not exist.", event.getBusId());
        }
    }
    
    @Override
    public void process(WaitingPassengersEvent event) {
        // Pass to the passengerQueryResults
        Simulation.getInstance().getLogger().log(Level.INFO, "Passenger query result received.");
        Bus.addQueryResult(event);
    }
    
    @Override
    public void process(ChangeReleasingFrequencyEvent event) {
        }
    }
    
    
    
    
    
    
     
}
