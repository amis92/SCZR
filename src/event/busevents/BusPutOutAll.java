package event.busevents;

import model.Bus;
import view.BusEvent;

/**
 * Created by ppeczek on 2014-05-23.
 */
public class BusPutOutAll extends BusEvent {
    public BusPutOutAll(Bus bus) {
        super(bus);
    }
}
