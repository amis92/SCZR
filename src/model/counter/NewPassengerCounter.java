package model.counter;

import event.guievents.NewPassengerEvent;
import main.SimulatorConstants;
import model.BusStop;
import model.Schedule;
import view.SimulatorEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by ppeczek on 2014-05-25.
 */
public class NewPassengerCounter extends CyclicCounter
{

    private BusStop busStop;
    private LinkedBlockingQueue<SimulatorEvent> blockingQueue;
    private int minGenerationTime;
    private int maxGenerationTime;

    public NewPassengerCounter(LinkedBlockingQueue<SimulatorEvent> blockingQueue, int value, BusStop busStop) {
        super(null, value, null);
        this.blockingQueue = blockingQueue;
        this.busStop = busStop;
        this.minGenerationTime = SimulatorConstants.defaultMinGenerationTime;
        this.maxGenerationTime = SimulatorConstants.defaultMaxGenerationTime;
    }

    public void throwEvent() {
        BusStop destination = randomDestinationBusStop();
        try
        {
            this.blockingQueue.put(new NewPassengerEvent(busStop.getNAME(), destination.getNAME()));
        } catch (final InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private BusStop randomDestinationBusStop() {
        ArrayList<BusStop> busStops = Schedule.getInstance().getPassengersStops();
        BusStop destination = null;
        do {
            Random randomGenerator = new Random();
            int randomIndex = randomGenerator.nextInt(busStops.size());
            destination = busStops.get(randomIndex);
        } while (destination.equals(busStop) || destination.equals(null));
        return destination;
    }

    public void finishedCounting() {
        max_value = randomMaxValue();
        reset();
        throwEvent();
    }

    public void setCounterBounds(int minValue, int maxValue)
    {
        minGenerationTime = minValue;
        maxGenerationTime = maxValue;
    }

    private int randomMaxValue() {
        Random randomGenerator = new Random();
        int newMaxValue;
        if (maxGenerationTime == minGenerationTime) {
            newMaxValue = minGenerationTime;
        } else {
             newMaxValue = randomGenerator.nextInt(maxGenerationTime - minGenerationTime)
                    + minGenerationTime + SimulatorConstants.randomTimeGenerationShift;
        }
        return newMaxValue;
    }

}
