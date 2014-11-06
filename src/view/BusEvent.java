package view;

import model.Bus;
import model.BusStop;

/**
 * Created by ppeczek on 2014-05-21.
 */
public class BusEvent extends SimulatorEvent {
    private Bus bus;
    private BusStop busStop;

    public BusEvent() {
    }

    public BusEvent(Bus bus) {
        this.bus = bus;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public BusStop getBusStop() {
        return busStop;
    }

    public void setBusStop(BusStop busStop) {
        this.busStop = busStop;
    }
}
