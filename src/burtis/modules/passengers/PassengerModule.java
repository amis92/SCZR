package burtis.modules.passengers;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * Logger.
     */
    private Logger logger = Logger
            .getLogger(PassengerModule.class.getName());
        
    /**
     * Managers.
     */
    private final Managers managers = new Managers();
       
    /**
     * Action executor.
     */
    private final ActionExecutor actionExecutor;

    /**
     * Current cycle of simulation.
     * 
     * Updated by {@link PassengerModuleEventHandler#process(burtis.common.events.flow.TickEvent)}.
     */
    private long currentCycle = 0;
        
    public PassengerModule(NetworkConfig networkConfig)
    {
        super(networkConfig.getModuleConfigs().get(NetworkConfig.PSNGR_MODULE));
        
        managers.setBusStopManager(new BusStopManager(managers));
        managers.setBusManager(new BusManager(managers));
        managers.setPassengerManager(new PassengerManager(managers));
        managers.setTransactionManager(new TransactionManager(managers));
        
        this.actionExecutor = new ActionExecutor(
                this.client, 
                networkConfig);
                
        this.eventHandler = new PassengerModuleEventHandler(
                this, 
                actionExecutor, 
                managers);
    }
    
/* ##############################################
 * GETTERS AND SETTERS
 * ########################################### */
    
    public long getCurrentCycle()
    {
        return currentCycle;
    }

    public void setCurrentCycle(long currentCycle)
    {
        this.currentCycle = currentCycle;
    }

/* ##############################################
 * END OF GETTERS AND SETTERS
 * ########################################### */

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

    /**
     * Returns mockup of the current simulation state.
     * 
     * @return mockup
     */
    public Mockup buildMockup(List<MockupBus> busMockups)
    {       
        // Add passengers to buses
        for (MockupBus busMockup : busMockups)
        {
            ArrayList<MockupPassenger> passengerList = new ArrayList<>();
            Bus bus;
            
            try {
                bus = managers.getBusManager().getBusById(busMockup.getId());
                
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
            catch (NoSuchBusException ex) {
                busMockup.setPassengerList(new ArrayList<>());
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
    
    /**
     * Main method for application.
     * 
     * @param args
     *            No parameters are expected.
     */
    public static void main(String[] args)
    {
        PassengerModule app = new PassengerModule(NetworkConfig.defaultConfig());
        app.main();
    }
}
