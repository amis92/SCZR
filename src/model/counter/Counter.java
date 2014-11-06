package model.counter;

import model.Bus;
import view.SimulatorEvent;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ppeczek on 2014-05-20.
 */
abstract public class Counter implements Serializable
{
    private int value;
    private boolean counted;
    private LinkedBlockingQueue<SimulatorEvent> blockingQueue;
    private final Bus bus;
    private boolean working = true;

    public Counter(LinkedBlockingQueue<SimulatorEvent> blockingQueue, int value, Bus bus) {
        this.value = value;
        this.counted = false;
        this.blockingQueue = blockingQueue;
        this.bus = bus;
    }

    public int countdown() {
//        if (isDownCounted()) {
//            throw new CounterNotInitiatedException();
//        }
        if (!working)
        {
            return value;
        }

        if (value > 0) {
            value--;
        }
//        else {
        if (value == 0) {
            setCounted(true);
            finishedCounting();
        }
        return value;
    }

    public void setWorking(boolean working)
    {
        this.working = working;
    }

    public void finishedCounting() {
        throwEvent();
    }

    public void throwEvent() {

    }

    public int getValue() {
        return value;
    }

    public void initiateCounter(int value) {
        this.counted = false;
        this.value = value;
    }

    public void setCounted(boolean counted) {
        this.counted = counted;
    }

    public boolean isDownCounted() {
        return counted;
    }

    public LinkedBlockingQueue<SimulatorEvent> getBlockingQueue() {
        return blockingQueue;
    }

    public Bus getBus() {
        return bus;
    }
}
