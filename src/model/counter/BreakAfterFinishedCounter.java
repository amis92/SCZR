package model.counter;

import model.Bus;
import model.BusDepot;
import model.BusState;
import view.SimulatorEvent;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ppeczek on 2014-05-24.
 */
public class BreakAfterFinishedCounter extends CyclicCounter implements Serializable
{
    public BreakAfterFinishedCounter(LinkedBlockingQueue<SimulatorEvent> blockingQueue, int cooldownAfterLoops, Bus bus) {
        super(blockingQueue, cooldownAfterLoops, bus);
    }

    public void throwEvent() {
        Bus bus = getBus();
        BusDepot.getInstance().getBusQueue().add(bus);
        bus.setState(BusState.READY_TO_GO);
        bus.setFinishedLoops(false);
//        try
//        {
//            getBlockingQueue().put(new BusReadyToGo(getBus()));
//        } catch (final InterruptedException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }
}
