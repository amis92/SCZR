package model.counter;

import model.Bus;
import view.SimulatorEvent;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ppeczek on 2014-05-24.
 */
public class LoopsCounter extends CyclicCounter {
    public LoopsCounter(LinkedBlockingQueue<SimulatorEvent> blockingQueue, int value, Bus bus) {
        super(blockingQueue, value, bus);
    }

    public void throwEvent() {;
        getBus().setFinishedLoops(true);
//        try {
//            getBlockingQueue().put(new BusComeBackSignal(getBus()));
//        } catch (final InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }
}
