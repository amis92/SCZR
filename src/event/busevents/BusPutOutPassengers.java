package event.busevents;

import model.Bus;
import view.BusEvent;

/**
 * Created by ppeczek on 2014-05-22.
 */
public class BusPutOutPassengers extends BusEvent {
    public BusPutOutPassengers(Bus bus) {
        super(bus);
    }
}
