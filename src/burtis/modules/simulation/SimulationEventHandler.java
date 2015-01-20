package burtis.modules.simulation;

import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;
import burtis.common.events.Passengers.WaitingPassengersEvent;
import burtis.common.events.Simulator.BusDeparturesEvent;
import burtis.common.events.Simulator.BusMockupsEvent;
import burtis.common.events.Simulator.ChangeReleasingFrequencyEvent;
import burtis.common.events.flow.ModuleReadyEvent;
import burtis.common.events.flow.TerminateSimulationEvent;
import burtis.common.events.flow.TickEvent;
import burtis.modules.simulation.models.BusManager;
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
    // private final BusStopManager busStopManager;
    /**
     * Reference to the depot.
     */
    // private final Depot depot;
    /**
     * Reference to the terminus.
     */
    private final Terminus terminus;
    
    /**
     * Event handler's logger.
     */
    private final Logger logger;

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
            final Depot depot,
            final Logger logger)
    {
        this.simulation = simulation;
        this.actionExecutor = actionExecutor;
        this.busManager = busManager;
        // this.busStopManager = busStopManager;
        // this.depot = depot;
        this.terminus = busStopManager.getTerminus();
        
        this.logger = logger;
    }

    @Override
    public void defaultHandle(SimulationEvent event)
    {
        logger.log(Level.WARNING, "Unknown event {0}", event.getClass()
                .getSimpleName());
    }

    @Override
    public void process(ModuleReadyEvent event)
    {
        //ignoring silently
    }
    
    @Override
    public void process(TerminateSimulationEvent event)
    {
        logger.info("TerminateSimulationEvent");
        simulation.shutdown();
    }

    /**
     * {@link TickEvent} handler.
     * 
     * Checks if simulation is in sync with tick events. It means
     * currentIteration must be one less than number received in the event.
     * 
     * Any exception caught during execution of this handler causes termination
     * of simulation.
     */
    @Override
    public void process(TickEvent event)
    {
        logger.info("TickEvent, iteration " + event.iteration());
        long iteration = event.iteration();
        try
        {
            simulation.setCurrentCycle(iteration);

            terminus.departBus();
            busManager.updateBusPositions();
            
            logger.info(busManager.toString());
            
            // This is "interiteration sync point" with PassengerModule.
            // Iteration will be continued after receiving response from
            // PassengerModule, namely it will be continued in the
            // WaitingPassengersEvent handler.
            actionExecutor.sendWaitingPassengersQueryRequest(busManager
                    .getBusStopsIdsList());
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "TE " + ex.getClass().getSimpleName());
            simulation.shutdown();
        }
    }

    /**
     * {@link WaitingPassengersEvent} handler.
     * 
     * Takes list containing information on waiting passengers and calls
     * {@link BusManager#processWaitingPassengersQueryResponse(java.util.Map) }
     * 
     * Any exception caught during execution of this handler causes termination
     * of simulation.
     * 
     */
    @Override
    public void process(WaitingPassengersEvent event)
    {
        logger.info("WaitingPassengersEvent " + event.getBusIdWaitingPassengersList());
        
        try
        {
            busManager.processWaitingPassengersQueryResponse(event
                    .getBusIdWaitingPassengersList());
            // This is "interiteration sync point" with PassengerModule.
            // Iteration will be continued after receiving response from
            // PassengerModule, namely it will be continued in the
            // BusDepartureEvent handler.
            actionExecutor.sendBusArrivalEvent(busManager.getBusArrivalsList());
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "WPE " + ex.getClass().getSimpleName());
            simulation.shutdown();
        }
    }

    /**
     * {@link BusDeparturesEvent} handler.
     * 
     * Takes list containing infomarion on bus departures delivered in the event
     * and calls {@link BusManager#processBusDeparturesList(java.util.List)}.
     * 
     * After that sends bus mockups {@link BusMockupsEvent} reflecting current
     * state of the fleet and, subsequently, sends {@link ModuleReadyEvent}.
     * 
     * Any exception caught during execution of this handler causes termination
     * of simulation.
     * 
     */
    @Override
    public void process(BusDeparturesEvent event)
    {
        logger.info("BusDeparturesEvent: " + event.getDeparturesList());
        try
        {
            busManager.processBusDeparturesList(event.getDeparturesList());
            actionExecutor.sendBusMockupEvent(simulation.getCurrentCycle(),
                    busManager.getBusMockups());
            //logger.info(busManager.getBusMockups().toString());
            actionExecutor.sendModuleReadyEvent(simulation.getCurrentCycle());
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "(" + simulation.getCurrentCycle() + ")" + " BDE: " 
                    + ex.getMessage() + " " + ex.getClass().getSimpleName(), ex);
            simulation.shutdown();
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
        logger.info("ChangeReleasingFrequencyEvent, NRF="
                + event.getNewReleasingFrequency());
        terminus.changeReleasingFrequency(event.getNewReleasingFrequency());
    }
}
