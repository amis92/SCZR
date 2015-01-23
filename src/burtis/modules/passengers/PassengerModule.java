package burtis.modules.passengers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.logging.LogFormatter;
import burtis.common.mockups.Mockup;
import burtis.common.mockups.MockupBus;
import burtis.common.mockups.MockupBusStop;
import burtis.common.mockups.MockupPassenger;
import burtis.modules.AbstractNetworkModule;
import burtis.modules.network.NetworkConfig;
import burtis.modules.passengers.exceptions.NoSuchBusException;
import burtis.modules.passengers.model.Bus;
import burtis.modules.passengers.model.BusManager;
import burtis.modules.passengers.model.BusStop;
import burtis.modules.passengers.model.BusStopManager;
import burtis.modules.passengers.model.Passenger;
import burtis.modules.passengers.model.PassengerManager;
import burtis.modules.passengers.model.TransactionManager;

/**
 * Module responsible for all passengers operations.
 * 
 * @author Mikołaj Sowiński
 */
public class PassengerModule extends AbstractNetworkModule
{
    /**
     * Main method for application.
     * 
     * @param args
     *            No parameters are expected.
     * @throws Exception
     *             - if the module setup failed.
     */
    public static void main(String[] args) throws Exception
    {
        PassengerModule app = new PassengerModule(NetworkConfig.defaultConfig());
        app.main();
    }

    /**
     * Action executor.
     */
    private final ActionExecutor actionExecutor;
    /**
     * Current cycle of simulation.
     * 
     * Updated by
     * {@link PassengerModuleEventHandler#process(burtis.common.events.flow.TickEvent)}
     * .
     */
    private long currentCycle = 0;
    /**
     * Logger.
     */
    private Logger logger = Logger.getLogger(PassengerModule.class.getName());
    /**
     * Managers.
     */
    private final Managers managers = new Managers();

    public PassengerModule(NetworkConfig networkConfig) throws Exception
    {
        super(networkConfig.getModuleConfigs().get(NetworkConfig.PSNGR_MODULE));
        managers.setBusStopManager(new BusStopManager(managers));
        managers.setBusManager(new BusManager(managers));
        managers.setPassengerManager(new PassengerManager(managers));
        managers.setTransactionManager(new TransactionManager(managers));
        managers.setLogger(logger);
        this.actionExecutor = new ActionExecutor(this.client, networkConfig);
        this.eventHandler = new PassengerModuleEventHandler(this,
                actionExecutor, managers);
        // debug use
        // addFileLoggerHandler();
    }

    /**
     * Creates a new mockup of current state, ready to be sent to other modules.
     * 
     * @param busMockups
     *            - provides list of buses received earlier, to be resent with
     *            main mockup.
     * 
     * @return mockup of the current simulation state.
     */
    public Mockup buildMockup(List<MockupBus> busMockups)
    {
        // Add passengers to buses
        for (MockupBus busMockup : busMockups)
        {
            ArrayList<MockupPassenger> passengerList = new ArrayList<>();
            Bus bus;
            try
            {
                bus = managers.getBusManager().getBusById(busMockup.getId());
                for (Passenger passenger : bus.getPassengers())
                {
                    passengerList.add(new MockupPassenger(passenger));
                }
                busMockup.setPassengerList(passengerList);
            }
            catch (NoSuchBusException ex)
            {
                logger.severe("No such bus " + ex.getBusId() + "!");
            }
        }
        // Create MockupBusStop list
        ArrayList<MockupBusStop> mockupBusStopArray = new ArrayList<>();
        for (BusStop busStop : managers.getBusStopManager().getBusStops())
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

    public long getCurrentCycle()
    {
        return currentCycle;
    }

    public void setCurrentCycle(long currentCycle)
    {
        this.currentCycle = currentCycle;
    }

    /**
     * Adds dumping logs to file.
     * 
     * @throws Exception
     *             when the filehanler couldn't be initialized or added to
     *             logger.
     */
    @SuppressWarnings("unused")
    private void addFileLoggerHandler() throws Exception
    {
        Handler handler;
        try
        {
            handler = new FileHandler(this.getClass().getName() + ".log");
            handler.setFormatter(new LogFormatter());
            logger.addHandler(handler);
        }
        catch (SecurityException | IOException e)
        {
            logger.severe(e.getMessage());
            throw e;
        }
    }

    @Override
    protected void init()
    {
        actionExecutor.sendModuleReadyEvent(currentCycle);
    }

    @Override
    protected void terminate()
    {
        logger.log(Level.INFO, "Terminating module...");
    }
}
