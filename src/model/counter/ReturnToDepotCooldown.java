package model.counter;

import event.busevents.BusReturnedToDepot;
import model.Bus;
import view.SimulatorEvent;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ppeczek on 2014-05-24.
 */
public class ReturnToDepotCooldown extends CyclicCounter {
    public ReturnToDepotCooldown(LinkedBlockingQueue<SimulatorEvent> blockingQueue, int value, Bus bus) {
        super(blockingQueue, value, bus);
    }

    public void throwEvent() {
        try {
            getBlockingQueue().put(new BusReturnedToDepot(getBus()));
        } catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
