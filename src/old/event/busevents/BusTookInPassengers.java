package event.busevents;

import model.Bus;
import view.BusEvent;

/**
 * Created by ppeczek on 2014-05-22.
 */
public class BusTookInPassengers extends BusEvent {
    public BusTookInPassengers(Bus bus) {
        super(bus);
    }
}
