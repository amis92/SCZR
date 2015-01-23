package burtis.modules.simulation.models;

import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.constants.SimulationModuleConsts;
import burtis.modules.simulation.exceptions.NoSuchBusStopException;

/**
 * Bus representation in simulation module.
 * 
 * @author Mikołaj Sowiński
 *
 */
public class Bus
{
    /**
     * Reference to the bus manager.
     */
    private final BusManager busManager;
    /**
     * Bus speed (units/iteration).
     */
    private final int busSpeed = SimulationModuleConsts.BUS_SPEED;
    /**
     * Reference to the bus stop manger.
     */
    private final BusStopManager busStopManager;
    /**
     * If bus awaits result of a query this value is not null and is set to the
     * BusStop object to be queried.
     */
    private BusStop busStopQueryRequest;
    /**
     * Bus stop bus is currently enqueued to. This value is null when bus is in
     * RUNNING state.
     */
    private BusStop currentBusStop;
    /**
     * Number of full line cycles.
     */
    private int cycle;
    /**
     * Determines if bus should be moved to the depot after arriving to the
     * terminus.
     */
    private boolean goToDepot;
    /**
     * Bus id.
     */
    private final int id;
    /**
     * Bus logger.
     */
    private final Logger logger;
    /**
     * Number of cycles to be done by default before moving to the depot.
     */
    private final int maxCycles = SimulationModuleConsts.BUS_MAX_CYCLES;
    /**
     * Next bus stop on the line.
     */
    private BusStop nearestBusStop;
    /**
     * Number of passengers in the bus.
     */
    private int numberOfPassengers;
    /**
     * Current bus position in units.
     */
    private int position;
    /**
     * Result of the query determining whether anyone is waiting at the bus
     * stop.
     * 
     * This value is set after receiving response from Passengers module.
     */
    private boolean queryResult;
    /**
     * Next bus stop defined by passenger's request. This value can be null (if
     * there is no passenger in the bus).
     */
    private BusStop requestedBusStop;
    /**
     * Current bus state.
     */
    private State state;

    /**
     * Creates new bus with next unique generated ID in {@link State#DEPOT} on
     * position 0.
     * 
     * @param busStopManager
     *            - Manager of bus stops.
     * @param busManager
     *            - Manager of buses.
     * @param logger
     *            - logger to be used.
     */
    public Bus(BusStopManager busStopManager, BusManager busManager,
            Logger logger)
    {
        this.id = IDGenerator.getNextId();
        this.state = State.DEPOT;
        this.position = 0;
        this.busStopManager = busStopManager;
        this.busManager = busManager;
        this.logger = logger;
    }

    /**
     * Function that makes bus arrived to the nearest bus stop.
     * 
     * Bus position is set to the bus stop's position (initially it can be
     * larger), bus state is changed and currentBusStop is set appropriately.
     */
    public void arrive()
    {
        currentBusStop = nearestBusStop;
        state = State.BUSSTOP;
        position = currentBusStop.getPosition();
        busManager.addBusArrival(this, currentBusStop);
    }

    /**
     * Function that makes bus arrived to the terminus.
     * 
     * Sets currentBusStop to the terminus (nearestBusStop) and sets bus state
     * to TERMINUS.
     * 
     * If maximum number of cycles is reached bus is reached goToDepot is set
     * true.
     */
    public void arriveAtTerminus()
    {
        // currentBusStop = nearestBusStop;
        if (cycle >= maxCycles)
        {
            goToDepot = true;
            state = State.DEPOT;
        }
        else
        {
            Terminus terminus = ((Terminus) nearestBusStop);
            terminus.enqueueBus(this);
            state = State.TERMINUS;
        }
    }

    /**
     * Function that orders bus depart from the bus stop.
     * 
     * It sets nextBusStop to the one given as an argument and calls
     * {@link BusStopManager#getNextBusStop(BusStop)} to set
     * {@link #nearestBusStop} value.
     * 
     * @param requestedBusStopName
     *            name of bus stop requested by passenger, can be null
     * @throws NoSuchBusStopException
     *             if current bus stop does not exist
     */
    public void depart(String requestedBusStopName)
            throws NoSuchBusStopException
    {
        if (state != State.BUSSTOP)
        {
            throw new RuntimeException("Invalid bus state!");
        }
        state = State.RUNNING;
        try
        {
            this.requestedBusStop = busStopManager
                    .getBusStopByName(requestedBusStopName);
        }
        catch (NoSuchBusStopException ex)
        {
            this.requestedBusStop = null;
        }
        this.nearestBusStop = busStopManager.getNextBusStop(currentBusStop);
        this.currentBusStop = null;
    }

    public BusStop getBusStopQueryRequest()
    {
        return busStopQueryRequest;
    }

    public BusStop getClosestBusStop()
    {
        return nearestBusStop;
    }

    public BusStop getCurrentBusStop()
    {
        return currentBusStop;
    }

    /**
     * Get number of free places in the bus.
     * 
     * @return number of free places
     */
    public int getFreePlaces()
    {
        return SimulationModuleConsts.BUS_CAPACITY - numberOfPassengers;
    }

    public int getId()
    {
        return id;
    }

    public BusStop getNextBusStop()
    {
        return requestedBusStop;
    }

    public int getNumberOfPassengers()
    {
        return numberOfPassengers;
    }

    public int getPosition()
    {
        return position;
    }

    public State getState()
    {
        return state;
    }

    /**
     * @return string containing bus id, its position and state.
     */
    public String getStateString()
    {
        return "BUS: " + id + ", POS: " + position + " S: " + state;
    }

    public boolean isGoToDepot()
    {
        return goToDepot;
    }

    /**
     * Function that makes bus omit nearest bus stop.
     * 
     * @throws NoSuchBusStopException
     *             if nearest bus stop does not exist
     */
    public void omitBusStop() throws NoSuchBusStopException
    {
        logger.info("Bus " + id + " ommits bus stop "
                + nearestBusStop.getName());
        nearestBusStop = busStopManager.getNextBusStop(nearestBusStop);
    }

    /**
     * Processes query (waiting passengers at bus stop) result.
     * 
     * If anyone is waiting calls {@link Bus#arrive()}, if not, calls
     * {@link Bus#omitBusStop()}.
     * 
     * Executes only in running state.
     * 
     * @throws NoSuchBusStopException
     *             if current bus stop does not exist
     */
    public void processQueryResult() throws NoSuchBusStopException
    {
        if (state != State.RUNNING)
            return;
        if (queryResult)
        {
            arrive(); // Arrives at the nearest bus stop
        }
        else
        {
            omitBusStop();
        }
    }

    /**
     * Sets bus to start at next iteration after arriving from the depot.
     * 
     * It means reset of cycle count and position as well as setting nearest bus
     * stop to the first bus stop in the line.
     */
    public void sendFromDepot()
    {
        if (state != State.DEPOT)
        {
            logger.log(
                    Level.WARNING,
                    "Bus {0} is not in a depot. However, it was to be sent from the depot...",
                    id);
        }
        else
        {
            cycle = 0;
            position = 0;
            state = State.RUNNING;
            requestedBusStop = null;
            nearestBusStop = busStopManager.getFirstBusStop();
            currentBusStop = null;
        }
    }

    /**
     * Sets bus to start at next iteration after being at the terminus.
     * 
     * It means increasing of cycle count, resetting position as well as setting
     * nearest bus stop to the first bus stop in the line.
     */
    public void sendFromTerminus()
    {
        if (state != State.TERMINUS)
        {
            logger.log(Level.WARNING,
                    "Bus {0} meant to be sent from Terminus isn't there.", id);
        }
        else
        {
            cycle++;
            position = 0;
            state = State.RUNNING;
            requestedBusStop = null;
            nearestBusStop = busStopManager.getFirstBusStop();
            currentBusStop = null;
        }
    }

    public void setClosestBusStop(BusStop closestBusStop)
    {
        this.nearestBusStop = closestBusStop;
    }

    public void setGoToDepot(boolean goToDepot)
    {
        this.goToDepot = goToDepot;
    }

    public void setNextBusStop(BusStop nextBusStop)
    {
        this.requestedBusStop = nextBusStop;
    }

    public void setNumberOfPassengers(int numberOfPassengers)
    {
        this.numberOfPassengers = numberOfPassengers;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public void setQueryResult(boolean queryResult)
    {
        this.queryResult = queryResult;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public String toString()
    {
        String currentBusStopName = currentBusStop == null ? "none"
                : currentBusStop.getName();
        String nearestBusStopName = nearestBusStop == null ? "none"
                : nearestBusStop.getName();
        // String requestedBusStopName = requestedBusStop == null ? "none" :
        // requestedBusStop.getName();
        return "Bus: " + id + " pos: " + position + " state:" + state
                + " nbs: " + nearestBusStopName + " cbs: " + currentBusStopName
                + "\n";
    }

    /**
     * Updates bus position and checks bus stop interaction.
     * 
     * If bus reaches bus stop and no one ordered stopping there, query is
     * requested to determine if stopping here is necessary.
     * 
     * @throws NoSuchBusStopException
     *             if one of the bus stops involved does not exist
     */
    public void updateBusPosition() throws NoSuchBusStopException
    {
        // This can be done only in running state
        if (state != State.RUNNING)
            return;
        position += busSpeed;
        busStopQueryRequest = null;
        if (position >= nearestBusStop.getPosition())
        {
            logger.info("Bus " + id + " reaches bus stop.");
            if (nearestBusStop instanceof Terminus)
            {
                arrive();
            }
            // If anyone ordered stopping here...
            else if (requestedBusStop == nearestBusStop)
            {
                arrive();
            }
            // None of present passengers wants to disembark here...
            else
            {
                // If there is any place left...
                if (getFreePlaces() > 0)
                {
                    // Want to know if anyone is waiting at the nearest bus stop
                    busStopQueryRequest = nearestBusStop;
                    return;
                }
                // If not, omit this damn bus stop
                else
                {
                    omitBusStop();
                }
            }
        }
    }

    /**
     * Enum describing bus state.
     */
    public enum State
    {
        /**
         * Bus at the bus stop.
         */
        BUSSTOP,
        /**
         * Bus that is in a depot.
         */
        DEPOT,
        /**
         * Bus on its way between bus stops.
         */
        RUNNING,
        /**
         * Bus waiting at the terminus.
         */
        TERMINUS
    }

    /**
     * Generates bus ids.
     */
    private static class IDGenerator
    {
        /**
         * Recent bus id.
         */
        private static int lastId = 0;

        /**
         * Returns id of the next bus.
         * 
         * @return new id
         */
        public static int getNextId()
        {
            return lastId++;
        }
    }
}
