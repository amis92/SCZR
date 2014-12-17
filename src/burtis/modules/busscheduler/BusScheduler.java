package burtis.modules.busscheduler;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

import burtis.common.constants.SimulationModuleConsts;
import burtis.common.events.AbstractEventHandler;
import burtis.common.events.MainMockupEvent;
import burtis.common.events.SimulationEvent;
import burtis.common.events.Simulator.BusStopsListEvent;
import burtis.common.events.Simulator.BusStopsListRequestEvent;
import burtis.common.events.Simulator.ChangeReleasingFrequencyEvent;
import burtis.common.events.flow.ModuleReadyEvent;
import burtis.common.mockups.Mockup;
import burtis.common.mockups.MockupBus;
import burtis.common.mockups.MockupBusStop;
import burtis.common.mockups.MockupPassenger;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.simulation.models.Bus;

import com.sun.istack.internal.logging.Logger;

/**
 * Schedules frequency with which Depot sends buses en route.
 * 
 * @author Kamil Drożdżał
 *
 */
public class BusScheduler extends AbstractEventHandler
{
    private static final Logger logger = Logger.getLogger(BusScheduler.class);
    /**
     * zmienna przechowująca ilość kroków potrzebną do jednokrotnego przejazdu
     * trasy (nie zlicza czasu potrzebnego na wsiadanie i wysiadanie pasażerów)
     */
    private int busStopCount;
    private final String moduleName;
    private final String[] receiverNames;
    private final Consumer<SimulationEvent> sender;

    public BusScheduler(ModuleConfig config, Consumer<SimulationEvent> sender)
    {
        List<ModuleConfig> configs = NetworkConfig.defaultConfig()
                .getModuleConfigs();
        this.moduleName = config.getModuleName();
        String receiverName = configs.get(NetworkConfig.SIM_MODULE)
                .getModuleName();
        this.receiverNames = new String[] { receiverName };
        this.sender = sender;
    }

    @Override
    public void defaultHandle(SimulationEvent event)
    {
        logger.log(Level.WARNING, "Unhandled event received: " + event);
    }

    @Override
    public void process(MainMockupEvent event)
    {
        optimize(event.getMainMockup());
    }

    @Override
    public void process(BusStopsListEvent event)
    {
        busStopCount = event.getBusStops().size();
    }

    private int countPassengersSitting(List<MockupBus> busList)
    {
        int passengersSittingTotal = 0;
        for (MockupBus bus : busList)
        {
            int passengerCount = bus.getPassengerList().size();
            if (bus.getState() == Bus.State.BUSSTOP
                    || bus.getState() == Bus.State.RUNNING)
            {
                passengersSittingTotal += passengerCount;
            }
        }
        return passengersSittingTotal;
    }

    private void makeDecision(Integer passengersSittingTotal,
            Integer waitingPassengersTotal, Long waitingTimeTotal)
    {
        int peopleInTheWorld = passengersSittingTotal + waitingPassengersTotal;
        int howManyBuses = (int) Math.ceil(peopleInTheWorld
                / (double) SimulationModuleConsts.BUS_CAPACITY);
        if (howManyBuses == 0)
        {
            setNewFrequency(0);
        }
        else
        {
            int newFrequency = (busStopCount * SimulationModuleConsts.BUS_MAX_CYCLES)
                    / (howManyBuses);
            setNewFrequency(newFrequency);
        }
        /**
         * TODO: Potencjalne problemy: - gdy nagle zmieni się sytuacja na
         * wymagającą większej ilości autobusów, wtedy Counter i tak będzie
         * musiał zliczyć do zera zanim zmieni się jego wartość. Może to
         * spowodować opóźnioną reakcję na zapotrzebowanie. - nie jest brany pod
         * uwagę czas potrzebny na wysiadanie i wsiadanie. Jeśli będzie to
         * proporcjojnalnie dużo w stosunku do czasu objazdu trasy, to trzeba
         * sprawdzić efekty :-)
         */
    }

    private void optimize(Mockup mockup)
    {
        List<MockupBus> busList = mockup.getBuses();
        List<MockupBusStop> busStops = mockup.getBusStops();
        int passengersSittingTotal = countPassengersSitting(busList);
        int waitingPassengersTotal = 0;
        Long waitingTimeTotal = 0L;
        for (MockupBusStop busStop : busStops)
        {
            waitingPassengersTotal += busStop.getPassengerCount();
            for (MockupPassenger passenger : busStop.getPassengers())
            {
                long waitingTime = passenger.getWaitingTime();
                logger.log(Level.FINE, "Passenger waiting time: " + waitingTime);
                waitingTimeTotal += waitingTime;
            }
        }
        makeDecision(passengersSittingTotal, waitingPassengersTotal,
                waitingTimeTotal);
    }

    private void setNewFrequency(int newFrequency)
    {
        logger.log(Level.INFO,
                "Sending calculated frequency [frames between bus releases]: "
                        + newFrequency);
        ChangeReleasingFrequencyEvent event;
        event = new ChangeReleasingFrequencyEvent(moduleName, receiverNames,
                newFrequency);
        sender.accept(event);
        sender.accept(new ModuleReadyEvent(moduleName));
    }

    /**
     * Retrieves list of bus stops to get their count.
     */
    public void init()
    {
        sender.accept(new BusStopsListRequestEvent(moduleName));
    }

    public void terminate()
    {
        logger.info("Terminating " + BusScheduler.class);
    }
}
