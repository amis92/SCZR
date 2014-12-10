package burtis.modules.simulation.models;

import burtis.common.constants.SimulationModuleConsts;
import burtis.modules.simulation.Simulation;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;

public class Terminus extends BusStop
{
    
    private static final Queue<Bus> buses = new LinkedList<>();
    private static long toTheNextDeparture = 0;
    
    private static long releasingFrequency = SimulationModuleConsts.TERMINUS_RELEASING_FREQUENCY;
    
    public static void changeReleasingFrequency(long newFrequency) {
        releasingFrequency = newFrequency;
    }

    public Terminus(int position, String name) {
        super(position, name);
    }
    
    public static void enqueueBus(Bus bus) {
        buses.add(bus);
    }
    
    /**
     * Departs buses at given intervals.
     * If bus is avaliable at the terminus it is departed. If not, bus is taken
     * from the depot. It there is no bus in the depot a warning is printed and
     * time interval is reseted.
     */
    public static void departBus() {
        Bus bus;
        // It's departure time! :D
        if(toTheNextDeparture == 0) {
            
            toTheNextDeparture = releasingFrequency;
            
            // Take first bus waiting at the terminus
            bus = buses.poll();
            // If there is any waiting            
            if(bus != null) {
                bus.sendFromTerminus();
            }
            // If there is no bus at the terminus
            else {
                bus = Depot.getBus();
                // If there is a bus in the depot
                if(bus != null) {
                    bus.sendFromDepot();
                }
                // No bus at all
                else {
                    Simulation.getInstance().getLogger().log(
                            Level.WARNING, "Bus was to be sent by the terminus, however no bus was available!");
                }
            }
        }
        // Still watiting...
        else {
            toTheNextDeparture--;
        }

    }
    
    
    

    
}
