package burtis.modules.simulation.models;

import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.constants.SimulationModuleConsts;
import burtis.modules.simulation.Simulation;
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
         * @return new id
         */
        public static int getNextId()
        {
            return lastId++;
        }
    }
    
    /**
     * Bus logger.
     */
    private final Logger logger;
    
    /**
     * Enum describing bus state.
     */
    public enum State
    {
        /**
         * Bus that is in a depot.
         */
        DEPOT, 
        
        /**
         * Bus at the bus stop.
         */
        BUSSTOP, 
        
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
     * Current bus state.
     */
    private State state;
    
    /**
     * Bus id.
     */
    private final int id;
    
    /**
     * Current bus position in units.
     */
    private int position;
        
    /**
     * Number of passengers in the bus.
     */
    private int numberOfPassengers;
    
    /**
     * Bus speed (units/iteration).
     */
    private final int busSpeed = SimulationModuleConsts.BUS_SPEED;
    
    /**
     * Next bus stop defined by passenger's request.
     * This value can be null (if there is no passenger in the bus).
     */
    private BusStop requestedBusStop;
    
    /**
     * Next bus stop on the line.
     */
    private BusStop nearestBusStop;
    
    /**
     * Bus stop bus is currently enqueued to.
     * This value is null when bus is in RUNNING state.
     */
    private BusStop currentBusStop;
       
    /**
     * Determines if bus should be moved to the depot after arriving to the
     * terminus.
     */
    private boolean goToDepot;
    
    /**
     * Number of full line cycles.
     */
    private int cycle;
    
    /**
     * Number of cycles to be done by default before moving to the depot.
     */
    private final int maxCycles = SimulationModuleConsts.BUS_MAX_CYCLES;
    
    /**
     * If bus awaits result of a query this value is not null and is set
     * to the BusStop object to be queried.
     */
    private BusStop busStopQueryRequest;
    
    /**
     * Result of the query determining whether anyone
     * is waiting at the bus stop.
     * 
     * This value is set after receiving response from Passengers module.
     */
    private boolean queryResult;
    
    /**
     * Reference to the bus manager.
     */
    private final BusManager busManager;
    
    /**
     * Reference to the bus stop manger.
     */
    private final BusStopManager busStopManager;
    
    /**
     * Constructor of Bus class. Buses are always created in DEPOT state.
     * 
     * @param capacity
     *            bus capacity
     */
    public Bus(BusStopManager busStopManager, BusManager busManager, Logger logger)
    {
        this.id = IDGenerator.getNextId();
        this.state = State.DEPOT;
        this.position = 0;
        this.busStopManager = busStopManager;
        this.busManager = busManager;
        this.logger = logger;
    }
    
/* ##############################################
 * GETTERS AND SETTERS
 * ########################################### */
    
    public BusStop getCurrentBusStop()
    {
        return currentBusStop;
    }
    
    public BusStop getBusStopQueryRequest()
    {
        return busStopQueryRequest;
    }

    public void setQueryResult(boolean queryResult)
    {
        this.queryResult = queryResult;
    }

    public int getNumberOfPassengers()
    {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(int numberOfPassengers)
    {
        this.numberOfPassengers = numberOfPassengers;
    }

    public BusStop getNextBusStop()
    {
        return requestedBusStop;
    }

    public void setNextBusStop(BusStop nextBusStop)
    {
        this.requestedBusStop = nextBusStop;
    }

    public BusStop getClosestBusStop()
    {
        return nearestBusStop;
    }

    public void setClosestBusStop(BusStop closestBusStop)
    {
        this.nearestBusStop = closestBusStop;
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public boolean isGoToDepot()
    {
        return goToDepot;
    }

    public void setGoToDepot(boolean goToDepot)
    {
        this.goToDepot = goToDepot;
    }

    public int getId()
    {
        return id;
    }

/* ##############################################
 * END OF GETTERS AND SETTERS
 * ########################################### */
    
    /**
     * Get number of free places in the bus.
     * 
     * @return number of free places
     */
    public int getFreePlaces()
    {
        return SimulationModuleConsts.BUS_CAPACITY - numberOfPassengers;
    }

    /**
     * Return string containing bus id, its position and state.
     * @return
     */
    public String getStateString()
    {
        return "BUS: " + id + ", POS: " + position + " S: " + state;
    }

    /**
     * Function that orders bus depart from the bus stop.
     * 
     * It sets nextBusStop to the one given as an argument and
     * calls {@link BusManager#findNearestBusStop} to get nearestBuStop
     * value.
     * 
     * @param requestedBusStop bus stop requested by passenger, can be null
     * @throws NoSuchBusStopException if current bus stop does not exist
     */
    public void depart(String requestedBusStopName) throws NoSuchBusStopException, Exception
    {
        if(state != State.BUSSTOP) {
            throw new Exception("Invalid bus state!");
        }
        
        state = State.RUNNING;
        try {
            this.requestedBusStop = busStopManager.getBusStopByName(requestedBusStopName);
        }
        catch (NoSuchBusStopException ex) {
            this.requestedBusStop = null;            
        }
        this.nearestBusStop = busStopManager.getNextBusStop(currentBusStop);
        this.currentBusStop = null;
    }
    
    /**
     * Function that makes bus arrived to the nearest bus stop.
     * 
     * Bus position is set to the bus stop's position
     * (initially it can be larger), bus state is changed 
     * and currentBusStop is set appropriately.
     */
    public void arrive() {
        currentBusStop = nearestBusStop;
        state = State.BUSSTOP;
        position = currentBusStop.getPosition();
        busManager.addBusArrival(this, currentBusStop);
    }
    
    /**
     * Function that makes bus arrived to the terminus.
     * 
     * Sets currentBusStop to the terminus (nearestBusStop)
     * and sets bus state to TERMINUS.
     * 
     * If maximum number of cycles is reached bus is 
     * reached goToDepot is set true.
     */
    public void arriveAtTerminus() {
        currentBusStop = nearestBusStop;
        state = State.TERMINUS;
        if(cycle >= maxCycles) {
            goToDepot = true;
        }
    }
    
    /**
     * Function that makes bus omit nearest bus stop.
     * 
     * @throws NoSuchBusStopException if nearest bus stop does not exist
     */
    public void omitBusStop() throws NoSuchBusStopException {
        logger.info("Bus " + id + " ommits bus stop " + nearestBusStop.getName());
        nearestBusStop = busStopManager.getNextBusStop(nearestBusStop);
    }
    
    /**
     * Updates bus position and checks bus stop interaction.
     * 
     * If bus reaches bus stop and no one ordered stopping there,
     * query is requested to determine if stopping here is necessary.
     * @throws NoSuchBusStopException if one of the bus stops involved does not exist
     */
    public void updateBusPosition() throws NoSuchBusStopException
    {
        
        // This can be done only in running state
        if(state != State.RUNNING) return;
        
        position += busSpeed;
        busStopQueryRequest = null;
        
        if(position >= nearestBusStop.getPosition()) {
            
            logger.info("Bus " + id + " reaches bus stop.");
            
            if(nearestBusStop instanceof Terminus) {
                arriveAtTerminus();
            }
            
            // If anyone ordered stopping here...
            else if(requestedBusStop == nearestBusStop) {
                arrive();
            }
            
            // None of present passengers wants to disembark here...
            else {
                
                // If there is any place left...
                if(getFreePlaces() > 0) {
                    // Want to know if anyone is waiting at the nearest bus stop
                    busStopQueryRequest = nearestBusStop;
                    return;
                }
                
                // If not, omit this damn bus stop
                else {
                    omitBusStop();
                }
            }
        }

    }
    
    /**
     * Processes query (waiting passengers at bus stop) result.
     * 
     *  If anyone is waiting calls {@link Bus#arrive()}, if not,
     *  calls {@link Bus#omitBusStop()}.
     * @throws NoSuchBusStopException if current bus stop does not exist
     */
    public void processQueryResult() throws NoSuchBusStopException {
        
        if(state != State.RUNNING) return;
        
        if(queryResult) {
            arrive(); // Arrives at the nearest bus stop
        } 
        else {
            omitBusStop();
        }
        
    }

    /**
     * Sets bus to start at next iteration after arriving from the depot.
     * 
     * It means reset of cycle count and position as well as setting nearest
     * bus stop to the first bus stop in the line.
     * @throws NoSuchBusStopException 
     */
    public void sendFromDepot() throws NoSuchBusStopException
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
     * It means increasing of cycle count, resetting position as well as setting nearest
     * bus stop to the first bus stop in the line.
     */
    public void sendFromTerminus()
    {
        if (state != State.TERMINUS)
        {
            logger.log(Level.WARNING,
                    "Bus {0} meant to be sent from Terminus isn't there.",
                    id);
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
    
    public String toString() {
        String currentBusStopName = currentBusStop == null ? "none" : currentBusStop.getName();
        String nearestBusStopName = nearestBusStop == null ? "none" : nearestBusStop.getName();
        String requestedBusStopName = requestedBusStop == null ? "none" : requestedBusStop.getName();
        return "Bus: " + id + " pos: " + position + " state:" + state + " nbs: " + nearestBusStopName + " cbs: " + currentBusStopName + "\n";
    }

}
