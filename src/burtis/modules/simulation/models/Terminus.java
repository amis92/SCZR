package burtis.modules.simulation.models;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.constants.SimulationModuleConsts;
import burtis.modules.simulation.exceptions.NoSuchBusStopException;

/**
 * Representation of terminus.
 * 
 * It is subclass of BusStop. Should be uniquely added at the <b>end</b> of the
 * line.
 * 
 * @author Mikołaj Sowiński
 *
 */
public class Terminus extends BusStop
{
    /**
     * Terminus logger.
     */
    private final Logger logger = Logger.getLogger(Terminus.class.getName());
    /**
     * Queue of buses at the terminus.
     * 
     * Using queue makes buses stand in a FIFO order.
     */
    private final Queue<Bus> buses = new LinkedList<>();
    /**
     * Number of iterations left to the next bus departure.
     * 
     * Used when scheduler module is not present.
     */
    private long toTheNextDeparture = 0;
    /**
     * Automatic buses releasing frequency.
     */
    private long releasingFrequency = SimulationModuleConsts.TERMINUS_RELEASING_FREQUENCY;
    /**
     * Reference to the depot.
     */
    private final Depot depot;

    /**
     * Constructor.
     * 
     * Essentially calls superclass constructor ({@link BusStop}) and sets
     * reference to the depot.
     * 
     * @param position
     *            terminus position
     * @param name
     *            terminus name
     * @param depot
     *            depot
     */
    public Terminus(int position, String name, Depot depot)
    {
        super(position, name);
        this.depot = depot;
    }

    /**
     * Changes automatic bus releasing frequency.
     * 
     * @param newFrequency
     *            new releasing frequency
     */
    public void changeReleasingFrequency(long newFrequency)
    {
        releasingFrequency = newFrequency;
        logger.info(String.format("New releasing frequency: %d",
                releasingFrequency));
    }

    /**
     * Adds given bus to the terminus queue.
     * 
     * @param bus
     *            bus to be added
     */
    public void enqueueBus(Bus bus)
    {
        buses.add(bus);
    }

    /**
     * Checks if bus should depart at current iteration and if so orders it to
     * go.<br>
     * 
     * If bus is available at the terminus is ordered to go. If not, bus is
     * taken from the depot. It there is no bus in the depot a warning is
     * logged.<br>
     * 
     * No matter what action is taken, if depart was scheduled for current
     * iteration toTheNextDeparture variable is reseted to the
     * releasingFrequency.
     * 
     * @throws NoSuchBusStopException
     *             - when there was error with bus stop list.
     */
    public void departBus() throws NoSuchBusStopException
    {
        Bus bus;
        // It's departure time! :D
        if (toTheNextDeparture == 0)
        {
            toTheNextDeparture = releasingFrequency;
            // Take first bus waiting at the terminus
            bus = buses.poll();
            // If there is any waiting
            if (bus != null)
            {
                bus.sendFromTerminus();
                logger.info(String.format(
                        "Sent bus from TERMINUS, bus ID = %d", bus.getId()));
            }
            // If there is no bus at the terminus
            else
            {
                bus = depot.getBus();
                // If there is a bus in the depot
                if (bus != null)
                {
                    bus.sendFromDepot();
                    logger.info(String.format(
                            "Sent bus from DEPOT, bus ID = %d", bus.getId()));
                }
                // No bus at all
                else
                {
                    logger.log(Level.WARNING,
                            "Bus was to be sent by the terminus, however no bus was available!");
                }
            }
        }
        // Not now...
        else
        {
            toTheNextDeparture--;
        }
    }
}
