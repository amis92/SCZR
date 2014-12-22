package burtis.modules.simulation;

import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.constants.SimulationModuleConsts;
import burtis.common.events.SimulationEvent;
import burtis.common.events.Passengers.WaitingPassengersEvent;
import burtis.common.mockups.MockupBus;
import burtis.modules.AbstractNetworkModule;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.passengers.PassengerModule;
import burtis.modules.simulation.models.Bus;
import burtis.modules.simulation.models.BusManager;
import burtis.modules.simulation.models.BusStop;
import burtis.modules.simulation.models.Depot;
import burtis.modules.simulation.models.Terminus;

/**
 * Simulation module.
 * 
 * Contains depot, terminus, buses and bus stops. No passengers here.
 * Communicates with passenger module to obtain information on passengers
 * waiting at bus stops.
 * 
 * @author Mikołaj Sowiński
 *
 */
public class Simulation extends AbstractNetworkModule
{
    private static final Logger logger = Logger.getLogger(Simulation.class
            .getName());
    /**
     * Current cycle. Positive value mens in cycle, negative in between.
     */
    private long currentCycle;
    private int lineLength = 0;
    private final ActionExecutor actionExecutor;
    private final BusManager busManager = new BusManager();

    public int getLineLength()
    {
        return lineLength;
    }

    public Logger getLogger()
    {
        return logger;
    }

    public ModuleConfig getModuleConfig()
    {
        return moduleConfig;
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
     * Sends list of bus mockups ({@link MockupBus} to the
     * {@link PassengerModule}.
     */
    public void sendBusMockups()
    {
        actionExecutor
                .sendBusMockupEvent(currentCycle, busManager.getMockups());
    }

    /**
     * Sends CycleCompletedEvent and zeros currentCycle variable.
     */
    public void sendCycleCompleted()
    {
        actionExecutor.sendCycleCompletedEvent(currentCycle);
        currentCycle = 0;
    }

    /**
     * Creates a new simulation.
     */
    private Simulation(NetworkConfig netConfig)
    {
        super(netConfig.getModuleConfigs().get(NetworkConfig.SIM_MODULE));
        this.actionExecutor = new ActionExecutor(this.client, netConfig);
        this.eventHandler = new SimulationEventHandler(this, actionExecutor,
                busManager);
    }

    @Override
    protected void init()
    {
        // Create bus stops
        BusStop.add(new Terminus(0, "Bielańska"));
        BusStop.add(30, "Plac Zamkowy");
        BusStop.add(60, "Hotel Bristol");
        BusStop.add(90, "Uniwersytet");
        BusStop.add(120, "Ordynacka");
        BusStop.add(150, "Foksal");
        BusStop.add(180, "Plac Trzech Krzyży");
        BusStop.add(210, "Plac na Rozdrożu");
        BusStop.add(240, "Plac Unii Lubelskiej");
        BusStop.add(270, "Rakowiecka");
        BusStop.add(new Terminus(300, "Bielańska"));
        Bus bus;
        for (int i = 0; i < SimulationModuleConsts.NUMBER_OF_BUSES; i++)
        {
            bus = busManager.add(SimulationModuleConsts.BUS_CAPACITY);
            Depot.putBus(bus);
        }
        lineLength = 300;
    }

    /**
     * Searches incoming event queue for WaitingPassengersEvent. Event handler
     * can not be used here as main thread is blocked in serial execution of
     * other event handler.
     * 
     * @return WaitingPassengersEvent or null if none is found
     */
    public WaitingPassengersEvent getWaitingPassengersEvent()
    {
        WaitingPassengersEvent waitingPassengersEvent;
        if (client.getIncomingQueue().size() > 0)
        {
            for (SimulationEvent event : client.getIncomingQueue())
            {
                if (event instanceof WaitingPassengersEvent)
                {
                    waitingPassengersEvent = (WaitingPassengersEvent) event;
                    client.getIncomingQueue().remove(event);
                    return waitingPassengersEvent;
                }
            }
            return null;
        }
        else
        {
            return null;
        }
    }

    @Override
    protected void terminate()
    {
        logger.log(Level.INFO, "Terminating module...");
    }

    /**
     * Main method for application.
     * 
     * @param args
     *            No parameters are expected.
     */
    public static void main(String[] args)
    {
        Simulation app = new Simulation(NetworkConfig.defaultConfig());
        app.main();
    }
}
