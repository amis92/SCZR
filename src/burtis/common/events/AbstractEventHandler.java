package burtis.common.events;

import burtis.common.events.flow.CycleCompletedEvent;
import burtis.common.events.flow.DoStepEvent;
import burtis.common.events.flow.ModuleReadyEvent;
import burtis.common.events.flow.PauseSimulationEvent;
import burtis.common.events.flow.StartSimulationEvent;
import burtis.common.events.flow.TerminateSimulationEvent;
import burtis.common.events.flow.TickEvent;
import burtis.common.events.passengers.PassengerGenerationRateConfigurationEvent;
import burtis.common.events.passengers.PassengerInfoRequestEvent;
import burtis.common.events.passengers.WaitingPassengersEvent;
import burtis.common.events.passengers.WaitingPassengersRequestEvent;
import burtis.common.events.simulation.BusArrivesAtBusStopEvent;
import burtis.common.events.simulation.BusDepartEvent;
import burtis.common.events.simulation.BusMockupsEvent;
import burtis.common.events.simulation.BusStateEvent;
import burtis.common.events.simulation.BusStopsListEvent;
import burtis.common.events.simulation.BusStopsListRequestEvent;
import burtis.common.events.simulation.ChangeReleasingFrequencyEvent;
import burtis.common.events.simulation.SendBusEvent;
import burtis.common.events.simulation.WithdrawBusEvent;

/**
 * Provides default processing abilities for all events. Inheriting classes
 * should override process method for interesting events.
 * 
 * @author Amadeusz Sadowski
 *
 */
public abstract class AbstractEventHandler
{
    /**
     * Called in every process function in {@link AbstractEventHandler}.
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
    
    public void process(ChangeReleasingFrequencyEvent event)
    {
        defaultHandle(event);
    }
    
    public void process(BusMockupsEvent event)
    {
        defaultHandle(event);
    }
    
}
