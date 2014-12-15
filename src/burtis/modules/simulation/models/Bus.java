package burtis.modules.simulation.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import burtis.common.constants.SimulationModuleConsts;
import burtis.common.events.Passengers.WaitingPassengersEvent;
import burtis.common.events.Passengers.WaitingPassengersRequestEvent;
import burtis.common.mockups.MockupBus;
import burtis.modules.simulation.Simulation;

public class Bus
{
    private static final List<Bus> buses = new ArrayList<>();
    /**
     * Queue for the results of querying passenger module.
     */
    private final static BlockingQueue<WaitingPassengersEvent> passengerQueryResults = new LinkedBlockingQueue<>(
            1);

    public enum State
    {
        DEPOT, BUSSTOP, RUNNING, TERMINUS
    }

    private State state;
    private final int id;

    public BusStop getCurrentBusStop()
    {
        return currentBusStop;
    }

    /**
     * Maximum bus capacity.
     */
    private final int capacity;
    /**
     * Number of passengers in the bus.
     */
    private int numberOfPassengers;
    /**
     * Bus speed (units/iteration).
     */
    private final static int busSpeed = SimulationModuleConsts.BUS_SPEED;
    /**
     * Next bus stop defined by passengers.
     */
    private BusStop nextBusStop;
    /**
     * Next bus stop on the line.
     */
    private BusStop closestBusStop;
    private BusStop currentBusStop;
    // /**
    // * Departure iteration.
    // */
    // private long startAt;
    /**
     * Determines if bus should be moved to the depot after arriving to the
     * terminus.
     */
    private boolean goToDepot;
    /**
     * Number of full line cycle.
     */
    private int cycle;
    /**
     * Number of cycles to be done by default.
     */
    private final int maxCycles = SimulationModuleConsts.BUS_MAX_CYCLES;
    /**
     * Current bus position.
     */
    private int position = 0;

    /**
     * Generates bus ids.
     */
    private static class IDGenerator
    {
        private static int lastId = 0;

        public static int getNextId()
        {
            return lastId++;
        }
    }

    /**
     * Constructor of Bus class. Buses are always created in DEPOT state.
     * 
     * @param capacity
     *            bus capacity
     */
    public Bus(int capacity)
    {
        id = IDGenerator.getNextId();
        state = State.DEPOT;
        this.capacity = capacity;
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
        return nextBusStop;
    }

    public void setNextBusStop(BusStop nextBusStop)
    {
        this.nextBusStop = nextBusStop;
    }

    public BusStop getClosestBusStop()
    {
        return closestBusStop;
    }

    public void setClosestBusStop(BusStop closestBusStop)
    {
        this.closestBusStop = closestBusStop;
    }

    /**
     * Get number of free places in the bus.
     * 
     * @return number of free places
     */
    public int getFreePlaces()
    {
        return capacity - numberOfPassengers;
    }

    /**
     * Get the value of state
     *
     * @return the value of state
     */
    public State getState()
    {
        return state;
    }

    /**
     * Set the value of state
     *
     * @param state
     *            new value of state
     */
    public void setState(State state)
    {
        this.state = state;
    }

    /**
     * Get the value of position
     *
     * @return the value of position
     */
    public int getPosition()
    {
        return position;
    }

    /**
     * Set the value of position
     *
     * @param position
     *            new value of position
     */
    public void setPosition(int position)
    {
        this.position = position;
    }

    /**
     * Get the value of goToDepot
     *
     * @return the value of goToDepot
     */
    public boolean isGoToDepot()
    {
        return goToDepot;
    }

    /**
     * Set the value of goToDepot
     *
     * @param goToDepot
     *            new value of goToDepot
     */
    public void setGoToDepot(boolean goToDepot)
    {
        this.goToDepot = goToDepot;
    }

    // /**
    // * Get the value of startAt
    // *
    // * @return the value of startAt
    // */
    // public long getStartAt() {
    // return startAt;
    // }
    //
    // /**
    // * Set the value of startAt
    // *
    // * @param startAt new value of startAt
    // */
    // public void setStartAt(long startAt) {
    // this.startAt = startAt;
    // }
    public int getId()
    {
        return id;
    }

    public String getStateString()
    {
        return "BUS: " + id + ", POS: " + position + " S: " + state;
    }

    public void depart(BusStop nextBusStop)
    {
        state = State.RUNNING;
        this.nextBusStop = nextBusStop;
        this.currentBusStop = null;
        this.closestBusStop = BusStop.getClosestBusStop(position);
    }

    // //////////////////////////////
    // ////// STATIC METHODS ////////
    // //////////////////////////////
    /**
     * Returns bus of given id.
     * 
     * @param busId
     *            bus id
     * @return Bus
     */
    public static Bus getBusById(int busId)
    {
        for (Bus bus : buses)
        {
            if (bus.getId() == busId)
                return bus;
        }
        return null;
    }

    /**
     * Adds new bus of given capacity. New buses are created in a depot state.
     * 
     * @param capacity
     *            bus capacity
     * @return newly created bus
     */
    public static Bus add(int capacity)
    {
        Bus newBus = new Bus(capacity);
        buses.add(newBus);
        return newBus;
    }

    /**
     * Updates bus positions according to its state.
     */
    public static void updatePositions()
    {
        for (Bus bus : buses)
        {
            // Running case
            if (bus.getState() == Bus.State.RUNNING)
            {
                calculatePosition(bus);
            }
        }
    }

    /**
     * Calculates next position of the bus. If one reaches bus stop, registers
     * it in the bus stop queue as well as changes its internal state to
     * BUSSTOP.
     * 
     * @param bus
     */
    private static void calculatePosition(Bus bus)
    {
        // Did we reach closest bus stop?
        BusStop closestBusStop = bus.getClosestBusStop();
        // If bus reaches bus stop in this iteration
        if (bus.getPosition() + busSpeed >= closestBusStop.getPosition())
        {
            // If nearest bus stop is terminus we are at the terminus
            if (bus.getClosestBusStop() instanceof Terminus)
            {
                // Withdraw to the depot if requested or max. number of cycles
                // achieved.
                if (bus.goToDepot || bus.cycle == bus.maxCycles)
                {
                    bus.setState(Bus.State.DEPOT);
                    Depot.putBus(bus);
                }
                // Enqueue to the terminus
                else
                {
                    bus.setState(State.TERMINUS);
                    Terminus.enqueueBus(bus);
                }
            }
            // Some passenger requested to stop here
            else if (bus.getClosestBusStop() == bus.getNextBusStop())
            {
                bus.setState(Bus.State.BUSSTOP);
                bus.currentBusStop = closestBusStop;
                bus.setPosition(closestBusStop.getPosition());
                BusStop.enqueueBus(bus, closestBusStop);
            }
            // If no one wanted to stop here maybe someone is waiting ...
            else
            {
                // If there is any place left
                if (bus.numberOfPassengers < bus.capacity)
                {
                    // It blocks! - If anyone is waiting stay at the bus stop.
                    if (queryForWaitingPassengers(bus.getClosestBusStop()))
                    {
                        bus.setState(Bus.State.BUSSTOP);
                        bus.currentBusStop = closestBusStop;
                        bus.setPosition(closestBusStop.getPosition());
                        BusStop.enqueueBus(bus, closestBusStop);
                    }
                    // If noone is waiting, ommit bus stop
                    else
                    {
                        bus.setPosition(bus.getPosition() + busSpeed);
                    }
                }
            }
        }
        else
        {
            bus.setPosition(bus.getPosition() + busSpeed);
        }
    }

    /**
     * Queries for number of waiting passengers at given bus stop.
     * 
     * @param busStop
     *            bus stop to be queried
     * @return true if at least one passenger is waiting
     */
    private static boolean queryForWaitingPassengers(BusStop busStop)
    {
        Simulation.getInstance().send(
                new WaitingPassengersRequestEvent(Simulation.getInstance()
                        .getModuleConfig().getModuleName(), busStop.getId()));
        Simulation
                .getInstance()
                .getLogger()
                .log(Level.INFO,
                        "Waiting for number of waiting passengers for bus stop {0}",
                        busStop.getName());
        WaitingPassengersEvent event;
        while (true)
        {
            event = Simulation.getInstance().getWaitingPassengersEvent();
            if (event != null)
            {
                break;
            }
        }
        return event.getWaitingPassengers() > 0;
    }

    /**
     * Withdraws bus of given id. Bus will be withdrawn to the depot at the
     * moment of the arrival to the terminus.
     * 
     * @param busId
     *            bus id
     */
    public static void withdrawBus(int busId)
    {
        Bus bus = getBusById(busId);
        if (bus != null)
        {
            bus.setGoToDepot(true);
        }
        else
        {
            Simulation.getInstance().getLogger()
                    .log(Level.WARNING, "No such bus {0}", busId);
        }
    }

    /**
     * Sets bus to start at next iteration after arriving from the depot. It
     * clears bus cycles count.
     * 
     * @param busId
     *            id of the bus
     */
    public static void sendFromDepot(int busId)
    {
        Bus bus = getBusById(busId);
        if (bus != null)
        {
            if (bus.state != State.DEPOT)
            {
                Simulation
                        .getInstance()
                        .getLogger()
                        .log(Level.WARNING,
                                "Bus {0} is not in a depot. However, it was to be sent from the depot...",
                                bus.id);
            }
            else
            {
                bus.cycle = 0;
                bus.setPosition(0);
                bus.depart(null);
            }
        }
        else
        {
            Simulation.getInstance().getLogger()
                    .log(Level.WARNING, "No such bus {0}", busId);
        }
    }

    /**
     * Sets bus to start at next iteration after arriving from the depot.
     */
    public void sendFromDepot()
    {
        sendFromDepot(id);
    }

    /**
     * Sets bus to start at next iteration after being at the terminus.
     * Increments bus cycles count.
     * 
     * @param busId
     *            id of the bus
     */
    public static void sendFromTerminus(int busId)
    {
        Bus bus = getBusById(busId);
        if (bus != null)
        {
            if (bus.state != State.TERMINUS)
            {
                Simulation
                        .getInstance()
                        .getLogger()
                        .log(Level.WARNING,
                                "Bus {0} is not in at the terminus. However, it was to be sent from the terminus...",
                                bus.id);
            }
            else
            {
                bus.cycle++;
                bus.setPosition(0);
                bus.depart(null);
            }
        }
        else
        {
            Simulation.getInstance().getLogger()
                    .log(Level.WARNING, "No such bus {0}", busId);
        }
    }

    /**
     * Sets bus to start at next iteration after being at the terminus.
     */
    public void sendFromTerminus()
    {
        sendFromTerminus(id);
    }

    /**
     * Adds query result to the results queue.
     * 
     * @param event
     *            {@link WaitingPassengersEvent}
     */
    public static void addQueryResult(WaitingPassengersEvent event)
    {
        passengerQueryResults.add(event);
    }

    /**
     * Generates list of {@link MockupBus}.
     * 
     * @return - list of {@link MockupBus}
     */
    public static List<MockupBus> getMockups()
    {
        List<MockupBus> mockups = new ArrayList<>();
        for (Bus bus : buses)
        {
            mockups.add(new MockupBus(bus.getId()));
        }
        return mockups;
    }
}
