package model;

import main.SimulatorConstants;
import mockup.Mockup;
import model.counter.BusReleaseCounter;
import view.SimulatorEvent;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import static main.SimulatorConstants.NumberOfBuses;

/**
 * Created by ppeczek on 2014-05-21.
 */
public class Model {

    private ArrayList<Bus> busContainer = new ArrayList<>();
    private Schedule schedule = Schedule.getInstance();
    private SimulationTimer simulationTimer = SimulationTimer.getInstance();
    private BusReleaseCounter busReleaseCounter;

    public Model(LinkedBlockingQueue<SimulatorEvent> blockingQueue) {
        BusDepot busDepot = BusDepot.getInstance();
        busReleaseCounter = new BusReleaseCounter(blockingQueue, SimulatorConstants.defaultBusReleaseCooldown);
        for (int i=0; i< NumberOfBuses; ++i) {
            Bus bus = new Bus(busDepot, blockingQueue);
            busContainer.add(bus);
            BusDepot.getInstance().getBusQueue().add(bus);
        }
        schedule.initBusStopPassengersCounters(blockingQueue);
    }

    public Mockup createMockup() {
        return new Mockup(getBusContainer(), schedule, simulationTimer.getTime());
    }

    public ArrayList<Bus> getBusContainer() {
        return busContainer;
    }

    public void step() {
        for (Bus bus : busContainer) {
            bus.move();
        }
        for (BusStop busStop : new ArrayList<BusStop>(schedule.getPassengersStops())) {
            busStop.getPassengerCounter().countdown();
        }
        busReleaseCounter.countdown();
        simulationTimer.go();

    }

    public final void generateSpecificPassenger(BusStop busStopStart, BusStop busStopStop)
    {
        busStopStart.queuePush(new Passenger(busStopStop));
    }

    public final void busReleaseCounterState(boolean working)
    {
        busReleaseCounter.setWorking(working);
    }

    public final void setBusReleaseCounterValue(int value)
    {
        busReleaseCounter.max_value = value;
    }

    public final void setNewPassengerCountersBound(int minValue, int maxValue)
    {
        for (BusStop busStop : new ArrayList<BusStop>(schedule.getPassengersStops())) {
            busStop.getPassengerCounter().setCounterBounds(minValue, maxValue);
            Schedule.getInstance().setCounterBounds(minValue, maxValue);
        }
    }

}
