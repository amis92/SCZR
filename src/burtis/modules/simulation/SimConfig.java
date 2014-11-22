/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.modules.simulation;

import burtis.modules.common.ModuleConfig;
import burtis.modules.simulation.models.BusStop;
import java.util.ArrayList;
import java.util.logging.Level;
import sun.util.logging.PlatformLogger;

/**
 *
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 */
public class SimConfig extends ModuleConfig {
        
    /**
     * Poll timeout in the simulation event loop given in ms.
     */
    private long pollTimeout = 300;
    
    /**
     * Number of buses.
     */
    private int nOfBuses = 10;
    
    /**
     * Bus stops.
     */
    private ArrayList<BusStop> busStops = new ArrayList<>();
    
    /**
     * Logger level.
     */
    private Level loggerLevel = Level.ALL;
    
    /**
     * Default constructor.
     * Initializes configuration to default values with 10 buses, no bus stops,
     * poll timeout of 300 ms as well as logger level set to ALL.
     */
    public SimConfig() {}
    
    /**
     * Constructor.
     * If null value is given a default value is taken.
     * @param busStops
     * @param nOfBuses
     * @param pollTimeout
     * @param loggerLevel 
     */
    public SimConfig(ArrayList<BusStop> busStops, int nOfBuses, long pollTimeout,
            Level loggerLevel, int port) {
        
        super("Simulation Module", String [] {"",""}, port);
        if(busStops != null) this.busStops = busStops;
        if(nOfBuses >= 0) nOfBuses = this.nOfBuses;
        if(pollTimeout >= 0) this.pollTimeout = pollTimeout;
        if(loggerLevel != null) this.loggerLevel = loggerLevel;        
        
    }
    
    public static SimConfig DefaultConfig() {
        return new SimConfig();
    }
    
    
    
}
