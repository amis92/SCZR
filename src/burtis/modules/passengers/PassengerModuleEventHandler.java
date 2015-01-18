package burtis.modules.passengers;

import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.MainMockupEvent;
import burtis.common.events.SimulationEvent;
import burtis.common.events.Passengers.NewPassengerEvent;
import burtis.common.events.Passengers.PassengerGenerationRateConfigurationEvent;
import burtis.common.events.Passengers.WaitingPassengersRequestEvent;
import burtis.common.events.Simulator.BusArrivalEvent;
import burtis.common.events.Simulator.BusMockupsEvent;
import burtis.common.events.flow.ModuleReadyEvent;
import burtis.common.events.flow.TerminateSimulationEvent;
import burtis.common.events.flow.TickEvent;
import burtis.common.mockups.Mockup;
import burtis.modules.passengers.exceptions.NoSuchBusStopException;
import burtis.modules.passengers.model.Bus;
import burtis.modules.passengers.model.BusStop;
import burtis.modules.passengers.model.BusStopManager;
import burtis.modules.passengers.model.PassengerManager;
import burtis.modules.simulation.Simulation;

/**
 * Event handler for Passenger Module.
 * 
 * @author Mikołaj Sowiński
 */
public class PassengerModuleEventHandler extends AbstractEventHandler
{
       
    /**
     * Event handler's logger.
     */
    private final Logger logger = Logger
            .getLogger(PassengerModule.class.getName());
    
    /**
     * Reference to the Passenger Module object.
     */
    private final PassengerModule passengerModule;
    
    /**
     * Reference to passenger module's action executor.
     */
    private final ActionExecutor actionExecutor;
    
    /**
     * Reference to managers.
     */
    private final Managers managers;
    
    /**
     * Constructor.
     * 
     * @param passengerModule
     * @param actionExecutor
     * @param managers
     */
    public PassengerModuleEventHandler(PassengerModule passengerModule,
            ActionExecutor actionExecutor, Managers managers)
    {
        super();
        this.passengerModule = passengerModule;
        this.actionExecutor = actionExecutor;
        this.managers = managers;
    }

    @Override
    public void defaultHandle(SimulationEvent event)
    {
        logger.log(Level.WARNING, "Unknown event {0}",
                event.getClass().getSimpleName());
    }
    
    @Override
    public void process(ModuleReadyEvent event)
    {
        // ignoring silently
    }

    /**
     * {@link TerminateSimulationEvent} handler.
     */
    @Override
    public void process(TerminateSimulationEvent event)
    {
        passengerModule.shutdown();
    }
    
    @Override
    public void process(NewPassengerEvent event)
    {
        BusStopManager stopManager = managers.getBusStopManager();
        PassengerManager passManager = managers.getPassengerManager();
        try
        {
            BusStop origin = stopManager.getBusStopByName(event.getOrigin());
            BusStop destination = stopManager.getBusStopByName(event.getDestination());
            passManager.addPassenger(origin, destination);
        }
        catch (NoSuchBusStopException e)
        {
            logger.warning("Failed to find stops sent for new passenger.");
        }
    }

    /**
     * {@link PassengerGenerationRateConfigurationEvent} handler.
     * 
     *  Invokes {@link PassengerManager#setGenerationCycleLength(int)} and {@link PassengerManager#setPassengersPerCycle(int)}
     *  to change passenger generation conditions.
     */
    @Override
    public void process(PassengerGenerationRateConfigurationEvent event)
    {
        logger.info("PassengerGenerationRateConfigurationEvent, "
                + "GCL=" + event.getGenerationCycleLength() 
                + " PPC=" + event.getPassengersPerCycle());
        managers.getPassengerManager().setGenerationCycleLength(event.getGenerationCycleLength());
        managers.getPassengerManager().setPassengersPerCycle(event.getPassengersPerCycle());
    }

    /**
     * {@link WaitingPassengersRequestEvent} handler.
     * 
     * Sends waiting passengers list.
     * 
     * @param event
     */
    @Override
    public void process(WaitingPassengersRequestEvent event)
    {
        logger.info("WaitingPassengersRequestEvent");
        actionExecutor.sendWaitingPassengersRequestResponse(managers.getBusStopManager().getWaitingPassengersMap());
    }

    /**
     * {@link TickEvent} handler.
     * 
     * Triggers passengers generation and ticks all existing transactions.
     */
    @Override
    public void process(TickEvent event)
    {   
        logger.info("TickEvent, iteration " + event.iteration());
        managers.getPassengerManager().generatePassengers();
        managers.getPassengerManager().updateWaitingTime();
        managers.getTransactionManager().tickTransactions();
        passengerModule.setCurrentCycle(event.iteration());
    }

    /**
     * {@link BusArrivalEvent} handler.
     * 
     * Removes finished transactions and enqueues buses to the bus stops what
     * triggers creating transactions. Next it sends list of departing buses.
     * 
     * @param event
     */
    @Override
    public void process(BusArrivalEvent event)
    {
        logger.info("BusArrivalEvent, listSize=" + event.getBusArrivalList().size());
        
        // Remove finished transactions
        // managers.getTransactionManager().removeFinishedTransactions();

        // Enqueue buses and create transactions
        for(Integer busId : event.getBusArrivalList().keySet()) {
            Bus bus = managers.getBusManager().add(busId);
            bus.arrive();
            try
            {
                BusStop busStop = managers.getBusStopManager().getBusStopByName(event.getBusArrivalList().get(busId));
                // Transactions are created here
                busStop.enqueueBus(bus);
            }
            catch (NoSuchBusStopException e)
            {
                logger.log(Level.SEVERE, "No bus stop of id " + event.getBusArrivalList().get(busId));
                passengerModule.terminate();
            }
        }
        
        actionExecutor.sendDeparturesList(managers.getBusManager().getBusDepartureInfoList());
    }

    /**
     * {@link BusMockupsEvent} handler.
     * 
     * Triggers sending of {@link MainMockupEvent} and {@link ModuleReadyEvent}.
     */
    @Override
    public void process(BusMockupsEvent event)
    {
        logger.info("BusMockupEvent");
        Mockup mockup = passengerModule.buildMockup(event.getBusMockups());
        logger.info(mockup.toString());
        actionExecutor.sendMainMockupEvent(mockup);
        actionExecutor.sendModuleReadyEvent(passengerModule.getCurrentCycle());
    }
}
