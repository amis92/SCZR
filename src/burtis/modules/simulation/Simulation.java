package burtis.modules.simulation;

import burtis.common.events.SimulationEvent;
import burtis.modules.network.client.Client;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.modules.simulation.models.*;
import java.util.ArrayList;

/**
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 *
 */
public class Simulation
{
    
    private final Logger logger;
    private final Level loggerLevel = Level.ALL;

    private final Depot depot;
    private final ArrayList<Bus> buses;
    private final ArrayList<BusStop> busStops;
    
    Client<SimulationEvent> client;
    
    SimConfig config;
    
    /**
     * Threads of the simulation.
     */
    private Thread eventLoop;
    private Thread simulationLoop;
    
    /**
     * Number of buses in the system.
     */
    private final int N = 10;
       
    /**
     * Creates a new simulation.
     */
    public Simulation() {
        
        logger = Logger.getLogger(Simulation.class.getName());
        logger.setLevel(Level.INFO);
        
        config = SimConfig.DefaultConfig();
        
        client = new Client("127.0.0.1", config.getPort());
        
        depot = new Depot();
        buses = new ArrayList<Bus>();
        //busStops = config.
                        
        for(int i=0; i<N; i++) buses.add(new Bus());
                
    }
            
    /**
     * Prints all buses present in the simulation along with their id, position and state.
     */
    public void printBuses() {
        Iterator<Bus> it = buses.iterator();
        while(it.hasNext()) {
            logger.log(Level.INFO, it.next().getState());
        }
    }
    
    /**
     * Prints all bus stops present in the simulation along with their id, position, name and bus queue.
     */
    public void printBusStops() {
        Iterator<BusStop> it = busStops.iterator();
        while(it.hasNext()) {
            logger.log(Level.INFO, it.next().getState());
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
         */
        
    }
    
    private void eventLoop() {
        
        SimulationEvent event;
        
        event = 
        
        
        
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        
    }
    
}
