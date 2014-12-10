package burtis.common.events;

import burtis.common.events.Passengers.BusDepartEvent;
import burtis.common.events.Passengers.BusStopsListRequestEvent;
import burtis.common.events.Passengers.MainMockupEvent;
import burtis.common.events.Passengers.WaitingPassengersEvent;
import burtis.common.events.Simulation.BusArrivesAtBusStopEvent;
import burtis.common.events.Simulation.BusStateEvent;
import burtis.common.events.Simulation.BusStopsListEvent;
import burtis.common.events.Simulation.WaitingPassengersRequestEvent;
import burtis.common.events.Sync.TickEvent;
import burtis.common.events.gui.DoStepEvent;
import burtis.common.events.gui.PassengerGenerationRateConfigurationEvent;
import burtis.common.events.gui.PauseSimulationEvent;
import burtis.common.events.gui.StartSimulationEvent;

/**
 * Provides default processing abilities for all events. Inheriting classes
 * should override process method for interesting events.
 * 
 * @author Amadeusz Sadowski
 *
 */
public abstract class AbstractEventProcessor
{
    /**
     * Called in every process function in {@link AbstractEventProcessor}.
     * @param event
     */
    public abstract void defaultHandle(SimulationEvent event);
    
    public void process(ConfigurationEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(CycleCompletedEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(ModuleReadyEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(PassengerInfoRequestEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(SendBusEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(TerminateSimulationEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(WithdrawBusEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(DoStepEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(PassengerGenerationRateConfigurationEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(PauseSimulationEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(StartSimulationEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(BusDepartEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(BusStopsListRequestEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(WaitingPassengersEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(BusArrivesAtBusStopEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(BusStopsListEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(WaitingPassengersRequestEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(TickEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(BusStateEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(MainMockupEvent event)
    {
        defaultHandle(event);
    }
}
