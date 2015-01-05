package burtis.modules.simulation;

import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;
import burtis.common.events.Passengers.WaitingPassengersEvent;
import burtis.common.events.Simulator.BusDeparturesEvent;
import burtis.common.events.Simulator.BusMockupsEvent;
import burtis.common.events.Simulator.BusStopsListRequestEvent;
import burtis.common.events.Simulator.ChangeReleasingFrequencyEvent;
import burtis.common.events.flow.ModuleReadyEvent;
import burtis.common.events.flow.TerminateSimulationEvent;
import burtis.common.events.flow.TickEvent;
import burtis.common.mockups.MockupBus;
import burtis.modules.simulation.exceptions.NoSuchBusStopException;
import burtis.modules.simulation.exceptions.OutOfSyncException;
import burtis.modules.simulation.models.Bus;
import burtis.modules.simulation.models.BusManager;
import burtis.modules.simulation.models.BusStop;
import burtis.modules.simulation.models.BusStopManager;
import burtis.modules.simulation.models.Depot;
import burtis.modules.simulation.models.Terminus;

/**
 * Event handler for simulation module.
 * 
 * @author Mikołaj Sowiński
 */
class SimulationEventHandler extends AbstractEventHandler
{
    /**
     * Reference to the simulation module object.
     */
    private final Simulation simulation;
    
    /**
     * Reference to simulation's action executor.
     */
    private final ActionExecutor actionExecutor;
    
    /**
     * Reference to bus manager.
     */
    private final BusManager busManager;
    
    /**
     * Reference to bus stop manager.
     */
    private final BusStopManager busStopManager;
        
    /**
     * Reference to the depot.
     */
    private final Depot depot;
    
    /**
     * Reference to the terminus.
     */
    private final Terminus terminus;
    
    /**
     * Event handler's logger.
     */
    private final Logger logger = Logger
            .getLogger(SimulationEventHandler.class.getName());

    /**
     * Constructor.
     * 
     * @param simulation
     * @param actionExecutor
     * @param busManager
     * @param busStopManager
     * @param depot
     */
    public SimulationEventHandler(
            final Simulation simulation,
            final ActionExecutor actionExecutor, 
            final BusManager busManager,
            final BusStopManager busStopManager,
            final Depot depot)
    {
        this.simulation = simulation;
        this.actionExecutor = actionExecutor;
        this.busManager = busManager;
        this.busStopManager = busStopManager;
        this.depot = depot;
        this.terminus = busStopManager.getTerminus();
    }

    @Override
    public void defaultHandle(SimulationEvent event)
    {
        logger.log(Level.WARNING, "Unknown event {0}", event.getClass()
                .getSimpleName());
    }

    @Override
    public void process(TerminateSimulationEvent event)
    {
        simulation.terminate();
    }

    /**
     * {@link TickEvent} handler.
     * 
     * Checks if simulation is in sync with tick events. It means currentIteration
     * must be one less than number received in the event. 
     * 
     * Any exception caught during execution of this handler causes termination of
     * simulation.
     */
    @Override
    public void process(TickEvent event)
    {
        long iteration = event.iteration();
        try {
            // Sync check
            if(iteration != simulation.getCurrentCycle()+1) {
                throw new OutOfSyncException("Simulation");
            }
            else {
                simulation.setCurrentCycle(iteration);
            }
            
            terminus.departBus();

            busManager.updateBusPositions();
            
            // This is "interiteration sync point" with PassengerModule.
            // Iteration will be continued after receiving response from
            // PassengerModule, namely it will be continued in the 
            // WaitingPassengersEvent handler.
            actionExecutor.sendWaitingPassengersQueryRequest(busManager.getBusStopsIdsList());
        }
        catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getClass().getSimpleName());
            simulation.terminate();
        }

    }
    
    /**
     * {@link WaitingPassengersEvent} handler.
     * 
     * Takes list containing information on waiting passengers and calls
     * {@link BusManager#processWaitingPassengersQueryResponse(java.util.Map) }
     * 
     * Any exception caught during execution of this handler causes termination of
     * simulation.
     * 
     */
    @Override
    public void process(WaitingPassengersEvent event)
    {
        try
        {
            busManager.processWaitingPassengersQueryResponse(event.getBusIdWaitingPassengersList());
            
            // This is "interiteration sync point" with PassengerModule.
            // Iteration will be continued after receiving response from
            // PassengerModule, namely it will be continued in the 
            // BusDepartureEvent handler.
            actionExecutor.sendBusArrivalEvent(busManager.getBusArrivalsList());
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, ex.getClass().getSimpleName());
            simulation.terminate();
        }
        
    }

    /**
     * {@link BusDeparturesEvent} handler.
     * 
     * Takes list containing infomarion on bus departures delivered in the event and calls
     * {@link BusManager#processBusDeparturesList(java.util.List)}.
     * 
     * After that sends bus mockups {@link BusMockupsEvent} reflecting current state of the 
     * fleet and, subsequently, sends {@link ModuleReadyEvent}.
     * 
     * Any exception caught during execution of this handler causes termination of
     * simulation.
     * 
     */
    @Override
    public void process(BusDeparturesEvent event)
    {
        try {
            busManager.processBusDeparturesList(event.getDeparturesList());
            
            actionExecutor.sendBusMockupEvent(simulation.getCurrentCycle(), busManager.getBusMockups());
            actionExecutor.sendModuleReadyEvent();            
        }
        catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getClass().getSimpleName());
            simulation.terminate();
        }
    }

    
    /**
     * {@link ChangeReleasingFrequencyEvent} handler.
     * 
     * Calls {@link Terminus#changeReleasingFrequency(long)} with argument
     * Retrieved from the event.
     * 
     */
    @Override
    public void process(ChangeReleasingFrequencyEvent event)
    {
        terminus.changeReleasingFrequency(event.getNewReleasingFrequency());
    }
}
