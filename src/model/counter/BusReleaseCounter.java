package model.counter;

import event.BusStartSignal;
import view.SimulatorEvent;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by tomasz on 25.05.2014.
 */
public class BusReleaseCounter extends CyclicCounter {

    public BusReleaseCounter(LinkedBlockingQueue<SimulatorEvent> blockingQueue, int releasePeriod) {
        super(blockingQueue, releasePeriod, null);
    }

    public void throwEvent() {
     try
        {
            getBlockingQueue().put(new BusStartSignal());
        } catch (final InterruptedException e)
        {
            // TODO Auto-generated catch block
           e.printStackTrace();
        }
    }
}
