package burtis.modules.simulation.models;

import java.util.Vector;

import burtis.modules.simulation.Simulation;
import burtis.modules.simulation.models.Bus;

public class Depot
{

    /**
     * Class holding data of the bus remaining in the depot.
     * 
     * @author Mikołaj Sowiński
     *
     */
    private class BusData {
        
        private final int busId;
        /**
         * Time of the last return of the bus.
         */
        private final long backTime;
        
        public BusData(Bus bus) {
            busId = bus.getId();
            backTime = Simulation.getSimulationTime();
        };
        
    }
    
    Vector<BusData> busesInDepot;
    
}
