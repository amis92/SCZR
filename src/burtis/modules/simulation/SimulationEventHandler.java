package burtis.modules.simulation;

import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;
import burtis.common.events.Passengers.WaitingPassengersEvent;
import burtis.common.events.Simulator.BusDepartEvent;
import burtis.common.events.Simulator.BusStopsListRequestEvent;
import burtis.common.events.Simulator.ChangeReleasingFrequencyEvent;
import burtis.common.events.flow.TerminateSimulationEvent;
import burtis.common.events.flow.TickEvent;
import burtis.modules.simulation.models.Bus;
import burtis.modules.simulation.models.BusManager;
import burtis.modules.simulation.models.BusStop;
import burtis.modules.simulation.models.Terminus;

/**
 * Event handler for simulation module.
 * 
 * @author Mikołaj Sowiński
 */
class SimulationEventHandler extends AbstractEventHandler
{
    private final Simulation simulation;
    private final ActionExecutor actionExecutor;
    private final BusManager busManager;
    private final Logger logger = Logger.getLogger(SimulationEventHandler.class
            .getName());

    public SimulationEventHandler(final Simulation simulation,
            final ActionExecutor actionExecutor, final BusManager busManager)
    {
        this.simulation = simulation;
        this.actionExecutor = actionExecutor;
        this.busManager = busManager;
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
        simulation.shutdown();
    }

    @Override
    public void process(BusStopsListRequestEvent event)
    {
        logger.log(Level.INFO, "Bus stops list requested from {0}.",
                event.sender());
        actionExecutor.sendBusStopsListEvent(event.sender());
    }

    @Override
    public void process(TickEvent event)
    {
        long eventIteration = event.iteration();
        long simIteration = simulation.getCurrentCycle();
        logger.log(Level.INFO, "Tick received, iteration {0}", eventIteration);
        if (simIteration > 0 && eventIteration != simIteration)
        {
            logger.log(Level.SEVERE,
                    "Tick received before completion of cycle. Terminating.");
            simulation.shutdown();
        }
        simulation.setCurrentCycle(eventIteration);
        Terminus.departBus();
        busManager.updatePositions();
        simulation.sendBusMockups();
        simulation.sendCycleCompleted();
    }

    @Override
    public void process(BusDepartEvent event)
    {
        logger.log(Level.INFO, "Bus {0} departs from the bus stop.",
                event.getBusId());
        Bus bus = busManager.getBusById(event.getBusId());
        if (bus != null)
        {
            bus.depart(BusStop.getBusStopById(event.getNextBusStopId()));
        }
        else
        {
            logger.log(Level.SEVERE, "Bus {0} does not exist.",
                    event.getBusId());
        }
    }

    @Override
    public void process(WaitingPassengersEvent event)
    {
        logger.log(Level.INFO, "Passenger query result received.");
        // TODO 
        //busManager.addQueryResult(event);
    }

    @Override
    public void process(ChangeReleasingFrequencyEvent event)
    {
        Terminus.changeReleasingFrequency(event.getNewReleasingFrequency());
    }
}
