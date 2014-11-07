package model;

import main.SimulatorConstants;
import view.SimulatorEvent;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ppeczek on 2014-05-21.
 */
public class Schedule {
    private static Schedule ourInstance = new Schedule();
    private int minPassengerGenerationValue;
    private int maxPassengerGenerationValue;
    private static ArrayList<BusStop> busStops;

    private Schedule() {
        minPassengerGenerationValue = SimulatorConstants.defaultMinGenerationTime;
        maxPassengerGenerationValue = SimulatorConstants.defaultMaxGenerationTime;
        busStops = new ArrayList<BusStop>();
        BusStop lastStop, currentStop;
        BusDepot busDepot = BusDepot.getInstance();
        BusTerminus busTerminus = BusTerminus.getInstance();
        busStops.add(busDepot);
        busStops.add(busTerminus);
        busDepot.setRoute(SimulatorConstants.depotTerminusDistance);
        lastStop = busTerminus;
        for (Pair<String, Integer> pair : SimulatorConstants.busStopSettings) {
            currentStop = new BusStop(pair.getKey());
            lastStop.setRoute(currentStop, pair.getValue());
            busStops.add(currentStop);
            lastStop = currentStop;
        }
        lastStop.setRoute(busTerminus, 3);
    }

    public static Schedule getInstance() {
        return ourInstance;
    }

    public void initBusStopPassengersCounters(LinkedBlockingQueue<SimulatorEvent> eventQueue) {
        for (BusStop stop : this.getPassengersStops()) {
            stop.createNewPassengerCounter(eventQueue, randomPassengerGenerationInterval());
        }
    }

    private int randomPassengerGenerationInterval() {
        Random randomGenerator = new Random();
        return randomGenerator.nextInt(getMaxPassengerGenerationValue() - getMinPassengerGenerationValue()) +
                getMinPassengerGenerationValue() + SimulatorConstants.randomTimeGenerationShift;
    }

    public static ArrayList<BusStop> getBusStops() {
        return busStops;
    }

    public ArrayList<BusStop> getPassengersStops() {
        ArrayList<BusStop> passengersStops = new ArrayList<BusStop>();
        for (BusStop bs : busStops) {
            if (!(bs instanceof BusDepot)) {
                passengersStops.add(bs);
            }
        }
        return passengersStops;
    }

    public BusStop findBusStop(String name) {
        for (BusStop bs : busStops) {
            if (bs.getNAME().equals(name)) {
                return bs;
            }
        }
        return null;
    }

    public int getMinPassengerGenerationValue() {
        return minPassengerGenerationValue;
    }

    public int getMaxPassengerGenerationValue() {
        return maxPassengerGenerationValue;
    }

    public void setCounterBounds(int minValue, int maxValue) {
        minPassengerGenerationValue = minValue;
        maxPassengerGenerationValue = maxValue;
    }


}
