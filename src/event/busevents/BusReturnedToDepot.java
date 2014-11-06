package event.busevents;

import model.Bus;
import view.BusEvent;

/**
 * Created by ppeczek on 2014-05-21.
 */
public class BusReturnedToDepot extends BusEvent {
    public BusReturnedToDepot(Bus bus) {
        super(bus);
    }
}
