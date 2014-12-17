package burtis.modules.passengers;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.events.Simulator.BusMockupsEvent;
import burtis.common.events.Simulator.BusStopsListRequestEvent;
import burtis.common.events.flow.CycleCompletedEvent;
import burtis.common.mockups.Mockup;
import burtis.common.mockups.MockupBus;
import burtis.common.mockups.MockupBusStop;
import burtis.common.mockups.MockupPassenger;
import burtis.modules.AbstractNetworkModule;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.simulation.Simulation;

/**
 * Module responsible for all passengers operations.
 * 
 * @author Mikołaj Sowiński
 */
public class PassengerModule extends AbstractNetworkModule
{
    private static final PassengerModule pm = new PassengerModule(NetworkConfig
            .defaultConfig().getModuleConfigs().get(NetworkConfig.PSNGR_MODULE));
    private Logger logger;
    /**
     * Current cycle. Positive value mens in cycle, negative in between.
     */
    protected static long currentCycle;
    /**
     * List of bus mockups for the current cycle. Delivered by
     * {@link Simulation} using {@link BusMockupsEvent}.
     */
    protected static List<MockupBus> busMockups;

    /**
     * State of the module. Init is the state before receiving a bus stop list
     * from simulation module.
     */
    public enum State
    {
        INIT, RUNNING
    }

    protected static State state = State.INIT;

    protected static State getState()
    {
        return state;
    }

    public static PassengerModule getInstance()
    {
        return pm;
    }

    public Logger getLogger()
    {
        return logger;
    }

    public ModuleConfig getModuleConfig()
    {
        return moduleConfig;
    }

    private PassengerModule(ModuleConfig config)
    {
        super(config);
        logger = Logger.getLogger(config.getModuleName());
    }

    @Override
    protected void init()
    {
        logger.log(Level.INFO, "Requesting bus stops list...(timeout 1min)");
        send(new BusStopsListRequestEvent());
    }

    @Override
    protected void terminate()
    {
        logger.log(Level.INFO, "Terminating module...");
        // TODO call close
    }

    public static void main(String[] args)
    {
        PassengerModule pm = PassengerModule.getInstance();
        pm.eventHandler = new PassengerModuleEventHandler();
        pm.main();
    }

    /**
     * Sends CycleCompletedEvent and zeros currentCycle variable.
     */
    public void sendCycleCompleted()
    {
        send(new CycleCompletedEvent(moduleConfig.getModuleName(), currentCycle));
        currentCycle = 0;
    }

    public static Mockup getMockup()
    {
        ArrayList<MockupBusStop> mockupBusStopArray = new ArrayList<>();
        // Add passengers to buses
        for (MockupBus busMockup : busMockups)
        {
            ArrayList<MockupPassenger> passengerList = new ArrayList<>();
            Bus bus = Bus.getBus(busMockup.getId());
            // Bus may not be yet registeded => no passengers
            if (bus != null)
            {
                List<Passenger> passengersInBus = bus.getPassengers();
                if (passengersInBus.size() > 0)
                {
                    for (Passenger passenger : passengersInBus)
                    {
                        passengerList.add(new MockupPassenger(passenger));
                    }
                }
                busMockup.setPassengerList(passengerList);
            }
            else
            {
                busMockup.setPassengerList(new ArrayList<>());
            }
        }
        // Create MockupBusStop list
        for (BusStop busStop : BusStop.getBusStops())
        {
            ArrayList<MockupPassenger> passengerList = new ArrayList<>();
            Queue<Passenger> passengersOnBusStop = busStop.getPassengerQueue();
            if (passengersOnBusStop.size() > 0)
            {
                for (Passenger passenger : passengersOnBusStop)
                {
                    passengerList.add(new MockupPassenger(passenger));
                }
            }
            mockupBusStopArray.add(new MockupBusStop(passengerList, busStop
                    .getName()));
        }
        return new Mockup((ArrayList<MockupBus>) busMockups,
                mockupBusStopArray, currentCycle);
    }
}
