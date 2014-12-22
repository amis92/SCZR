package burtis.common.events;

import burtis.common.events.Passengers.PassengerGenerationRateConfigurationEvent;
import burtis.common.events.Passengers.PassengerInfoRequestEvent;
import burtis.common.events.Passengers.WaitingPassengersEvent;
import burtis.common.events.Passengers.WaitingPassengersRequestEvent;
import burtis.common.events.Simulator.BusArrivalEvent;
import burtis.common.events.Simulator.BusDepartEvent;
import burtis.common.events.Simulator.BusMockupsEvent;
import burtis.common.events.Simulator.BusStateEvent;
import burtis.common.events.Simulator.BusStopsListEvent;
import burtis.common.events.Simulator.BusStopsListRequestEvent;
import burtis.common.events.Simulator.ChangeReleasingFrequencyEvent;
import burtis.common.events.Simulator.SendBusEvent;
import burtis.common.events.Simulator.WithdrawBusEvent;
import burtis.common.events.flow.CycleCompletedEvent;
import burtis.common.events.flow.DoStepEvent;
import burtis.common.events.flow.ModuleReadyEvent;
import burtis.common.events.flow.PauseSimulationEvent;
import burtis.common.events.flow.StartSimulationEvent;
import burtis.common.events.flow.TerminateSimulationEvent;
import burtis.common.events.flow.TickEvent;
import burtis.modules.simulation.exceptions.OutOfSyncException;

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
    
    public void process(BusArrivalEvent event)
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
