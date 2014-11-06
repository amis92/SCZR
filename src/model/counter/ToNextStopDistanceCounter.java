package model.counter;

import event.busevents.BusArrivesToBusStop;
import model.Bus;
import view.SimulatorEvent;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ppeczek on 2014-05-24.
 */
public class ToNextStopDistanceCounter extends Counter {
    public ToNextStopDistanceCounter(LinkedBlockingQueue<SimulatorEvent> blockingQueue, int distance, Bus bus) {
        super(blockingQueue, distance, bus);
    }

    public void throwEvent() {
        try
        {
            getBlockingQueue().put(new BusArrivesToBusStop(getBus()));
        } catch (final InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
