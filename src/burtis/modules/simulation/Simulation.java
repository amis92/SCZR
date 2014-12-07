package burtis.modules.simulation;

import burtis.common.events.BusStopInfo;
import burtis.common.events.BusStopsListEvent;
import burtis.common.events.CycleCompletedEvent;
import burtis.common.events.DeathErrorEvent;
import burtis.common.events.GetBusStopsEvent;
import burtis.common.events.GetPassengersOnBusStopEvent;
import burtis.common.events.PassengerTransaction;
import burtis.common.events.PassengerTransactionEvent;
import burtis.common.events.PassengersOnBusStopEvent;
import burtis.common.events.SendBusEvent;
import burtis.common.events.SimulationEvent;
import burtis.common.events.TerminateSimulationEvent;
import burtis.common.events.TickEvent;
import burtis.common.events.WithdrawBusEvent;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.network.client.ClientModule;
import burtis.modules.simulation.models.Bus;
import burtis.modules.simulation.models.BusStop;
import burtis.modules.simulation.models.Depot;
import burtis.modules.simulation.models.Terminus;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 *
 */
public class Simulation implements Runnable
{
    
    private final Logger logger;
    private final Level loggerLevel = Level.ALL;

    private final Depot depot;
    private final ArrayList<Bus> buses;
    private final List<BusStop> busStops;
    
    ClientModule client;
    
    ModuleConfig config;
    ModuleConfig psgrConfig;
    
    private long iteration;
    
    /**
     * Threads of the simulation.
     */
    private Thread eventLoop;
    private Thread nextStateThread;
    
    /**
     * Number of buses in the system.
     */
    private final int numberOfBuses = 10;
    
    /**
     * Bus speed (units/iteration).
     */
    private final int busSpeed = 3;
    
    /**
     * Bus capacity.
     */
    private final int busCapacity = 100;
    
    /**
     * Bus starting frequency.
     * Given in number of simulation iterations.
     */
    private int busStartingFrequency = 200;
    
    /**
     * Data for computation thread.
     */
    private BlockingQueue<SimulationEvent> queryPassengerNoQueue = new ArrayBlockingQueue<>(1);
    
    private BlockingQueue<SimulationEvent> incomingPassengerTransactions = new ArrayBlockingQueue<>(1);
    
    /**
     * Method for thread computing next simulation state.
     */
    private class ComputeNextState implements Runnable {

        @Override
        public void run() {
           /*
            Positions
            transactions (depot/busstop)
            
            */
            
            updateBusPositions();
            makeTransactions();
            //sheduledBuses();
            
            client.send(new CycleCompletedEvent(config.getModuleName(), iteration));
        }
        
    }
       
    /**
     * Creates a new simulation.
     */
    public Simulation() {
        
        logger = Logger.getLogger(Simulation.class.getName());
                
        config = NetworkConfig.defaultConfig().getModuleConfigs().get(4);
        psgrConfig = NetworkConfig.defaultConfig().getModuleConfigs().get(3);
        
        client = new ClientModule(config);
        
        depot = new Depot();
        buses = new ArrayList<Bus>();
        busStops = new ArrayList<BusStop>();
                        
        for(int i=0; i<numberOfBuses; i++) buses.add(new Bus(busCapacity));
                
    }
    
    /**
     * Updates position of the buses depending on their internal state.
     */
    public void updateBusPositions() {
                
        for(Bus bus : buses) {
            
            if(bus.getState() == Bus.State.RUNNING) {            
                calculatePosition(bus);
            }
            else if(bus.getState() == Bus.State.TERMINUS) {
                if(iteration >= bus.getStartAt()) {
                    bus.setState(Bus.State.RUNNING);
                    calculatePosition(bus);
                }
            }
        }
    }
    
    /**
     * Calculates next position of the bus.
     * If one reaches bus stop, registers it in the bus stop queue as well as 
     * changes its internal state to BUSSTOP.
     * @param bus 
     */
    public void calculatePosition(Bus bus) {
        
        // Did we reach closest bus stop?
        BusStop closestBusStop = getBusStopById(bus.getClosestBusStopId());
        
        if(bus.getPosition() + busSpeed >= closestBusStop.getPosition()) {
            
            // If nearest bus stop is terminus we are at the terminus
            if(getBusStopById(bus.getClosestBusStopId()) instanceof Terminus) {
                bus.setState(Bus.State.TERMINUS);
                bus.setPosition(0);
                getBusStopById(bus.getClosestBusStopId()).enqueueBus(bus.getId());
            }
                        
            // Some passenger requested to stop here
            else if(bus.getClosestBusStopId() == bus.getNextBusStopId()) {
                bus.setState(Bus.State.BUSSTOP);
                bus.setPosition(closestBusStop.getPosition());
                getBusStopById(bus.getClosestBusStopId()).enqueueBus(bus.getId());
            }
            
            // If no one wanted to stop here maybe someone is waiting ...
            else {
                // It blocks!
                if(queryForWaitingPassengers(bus.getClosestBusStopId())) {
                    bus.setState(Bus.State.BUSSTOP);
                    bus.setPosition(closestBusStop.getPosition());
                    getBusStopById(bus.getClosestBusStopId()).enqueueBus(bus.getId());
                }
                
                else {
                    bus.setPosition(bus.getPosition() + busSpeed);
                }
            }
        }
        else {
            bus.setPosition(bus.getPosition() + busSpeed);
        }
    }
    
    public boolean queryForWaitingPassengers(int busStopId) {
    
        PassengersOnBusStopEvent event;
        
        client.send(new GetPassengersOnBusStopEvent(config.getModuleName(), 
                new String[] { psgrConfig.getModuleName() }, busStopId));
        try {
            event = (PassengersOnBusStopEvent)queryPassengerNoQueue.take();
            return event.getPassengersNo() > 0;
        } catch (InterruptedException ex) {
            Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
   
    }
    
    private BusStop getBusStopById(int id) {
        for(BusStop busStop : busStops) {
            if(busStop.getId() == id) return busStop;
        }
        return null;
    }
    
    private Bus getBusById(int id) {
        for(Bus bus : buses) {
            if(bus.getId() == id) return bus;
        }
        return null;
    }
    
    /**
     * Iterates through bus stops and realizes transactions between buses and bus stops.
     */
    public void makeTransactions() {
        
        List<PassengerTransaction> transactions = new ArrayList<>();
        
        try {
            // Check queue for finished transactions
            incomingPassengerTransactions.poll(0, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(BusStop busStop : busStops) {
            // There are buses enqued
            if(busStop.getWaitingBuses().size() > 0) {
                // Negative processed bus id means no bus is processed at the moment
                if(busStop.getProcessedBusId() < 0) {
                    int firstBusId = (int)busStop.getWaitingBuses().poll();
                    
                    busStop.setProcessedBusId(firstBusId);
                    transactions.add(new PassengerTransaction(busStop.getId(), firstBusId, getBusById(firstBusId).getFreePlaces()));
                }
            }
        }
        
        // If there are any transactions send them to the passengers module
        if(transactions.size() > 0) client.send(new PassengerTransactionEvent(
                config.getModuleName(), new String[] {psgrConfig.getModuleName()},
                transactions));
    }
            
    /**
     * Prints all buses present in the simulation along with their id, position and state.
     */
    public void printBuses() {
        Iterator<Bus> it = buses.iterator();
        while(it.hasNext()) {
            logger.log(Level.INFO, it.next().getStateString());
        }
    }
    
    /**
     * Prints all bus stops present in the simulation along with their id, position, name and bus queue.
     */
    public void printBusStops() {
        for( BusStop entry : busStops) {
            logger.log(Level.INFO, entry.getState());
        }
    }
    
    private void simulationLoop() {
        
        /**
         * At the end of each loop all transactions are completed!
         * Tick represents certain quant of time. This is also the smallest 
         * possible operation time. Operations lengths are given as integral 
         * values representing number of time quanta.
         * 
         * Two separate threads:
         * 1. Event handling thread
         * 2. Simulation
         * 
         * Event handling loop:
         * - poll(timeout)
         * - handleEvent
         * 
         * Simulation loop:
         * - wait for trigger==true
         * - t0 = nanoTime
         * - update positions
         * - realize transactions
         * - check time
         * - send CycleCompletedEvent
         * 
         * Bus can be locked by transaction that is realized
         * 
         * Buses starts from terminus every busStartingFrequency. If there is at
         * least one bus at the terminus in startTime-50 it is given an appropriate 
         * startTime (in iteration no). If there is no bus at that moment a bus
         * is taken from the Depot. If there is no bus, next arrived is directed
         * on the line. If after scheduling there is any bus left at the terminus
         * it it is taken to the depot.
         */
        
    }
    
    private void eventLoop() {
        
        SimulationEvent event;
        
        while(!Thread.interrupted()) {
            
            try {
                event = client.getIncomingQueue().take();
                logger.log(Level.INFO, "Received event {0}", event.getClass().getSimpleName());
                handleEvent(event);
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                break;
            }

        }
        
    }
    
    private void handleEvent(SimulationEvent event) {
        
        if(event instanceof GetBusStopsEvent) {
            getBusStops((GetBusStopsEvent)event);
        }
        else if(event instanceof TerminateSimulationEvent) {
            terminateSimulation((TerminateSimulationEvent)event);
        }
        else if(event instanceof SendBusEvent) {
            sendBus((SendBusEvent)event);
        }
        else if(event instanceof WithdrawBusEvent) {
            withdrawBus((WithdrawBusEvent)event);
        }
        else if(event instanceof TickEvent) {
            tickEvent((TickEvent)event);
        }
        else if(event instanceof PassengersOnBusStopEvent) {
            passengersOnBusStop((PassengersOnBusStopEvent)event);
        }
        //else if(event instanceof PassengerTransactionEvent) {
        //    passengerTransaction((PassengerTransactionEvent)event);
        //}
        else {
            defaultHandler(event);
        }
        
    }
    
    private void defaultHandler(SimulationEvent event) {
        logger.log(Level.SEVERE, "Unhandled event {0}", event.getClass().getSimpleName());
    }
    
    private void passengersOnBusStop(PassengersOnBusStopEvent event) {
        try {
            queryPassengerNoQueue.put(event);
        } catch (InterruptedException ex) {
            Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    
    private void getBusStops(GetBusStopsEvent event) {
        
        String[] recipient = { event.sender() };
        List<BusStopInfo> busStopsInfo = new ArrayList<>();
        
        for(BusStop busStop : busStops) {
            busStopsInfo.add(new BusStopInfo(busStop.getId(), 
                    busStop.getName(), busStop.getPosition()));
        }
        
        client.send(new BusStopsListEvent(config.getModuleName(), recipient, busStopsInfo));        
        
    }
    
    private void terminateSimulation(TerminateSimulationEvent event) {
        
        logger.log(Level.WARNING, "Terminating simulation due to signal from {0}", 
                (event == null) ? "X" : event.sender());
        try {
            nextStateThread.interrupt();
            nextStateThread.join();
            logger.log(Level.INFO, "Simulation thread finished.");
            eventLoop.interrupt();
            eventLoop.join();
            logger.log(Level.INFO, "Event processing thread finished.");
        }
        catch(InterruptedException ex) {}
        finally {
            client.close();
            System.exit(0);
        }

    }
    
    /**
     * Sets given bus to start at next iteration.
     * @param event 
     */
    private void sendBus(SendBusEvent event) {
        
        for(Bus bus : buses) {
            if(bus.getId() == event.busId()) {
                if(bus.getState() == Bus.State.DEPOT || bus.getState() == Bus.State.TERMINUS) {
                    bus.setStartAt(iteration + 1);
                }
                else {
                    logger.log(Level.WARNING, "Bus {0} is in illegal state.",
                            event.busId());
                    client.send(new DeathErrorEvent(config.getModuleName(), 
                            new String[] {event.sender()}, 
                            "Bus " + event.busId() + " is in illegal state!"));
                }
            }
            else {
                logger.log(Level.WARNING, "No such bus {0}", event.busId());
                client.send(new DeathErrorEvent(config.getModuleName(), 
                        new String[] {event.sender()}, 
                        "Bus " + event.busId() + " does not exist!"));
            }
        }
        
    }
    
    /**
     * Sets given bus to go to the depot when it comes to terminus.
     * @param event 
     */
    private void withdrawBus(WithdrawBusEvent event) {
        
        for(Bus bus : buses) {
            if(bus.getId() == event.busId()) {
                if(bus.getState() == Bus.State.RUNNING || bus.getState() == Bus.State.BUSSTOP) {
                    bus.setGoToDepot(true);
                }
                else {
                    logger.log(Level.WARNING, "Bus {0} is in illegal state.",
                            event.busId());
                    client.send(new DeathErrorEvent(config.getModuleName(), 
                            new String[] {event.sender()}, 
                            "Bus " + event.busId() + " is in illegal state!"));
                }
            }
            else {
                logger.log(Level.WARNING, "No such bus {0}", event.busId());
                client.send(new DeathErrorEvent(config.getModuleName(), 
                        new String[] {event.sender()}, 
                        "Bus " + event.busId() + " does not exist!"));
            }
        }
        
    }
    
    /**
     * Computes next simulation state using ComputeNextState class.
     */
    private void tickEvent(TickEvent event) {
        
        if(nextStateThread.isAlive()) {
            logger.log(Level.SEVERE, "Not syncing! New tick event came before end of computations!");
            terminateSimulation(null);           
        }
        else {
            nextStateThread = new Thread(new ComputeNextState());
            nextStateThread.start();
        }
        
    }
    
    
    
    
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
