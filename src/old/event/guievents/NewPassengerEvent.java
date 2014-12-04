package event.guievents;

import view.SimulatorEvent;

import java.io.Serializable;

/**
 * Created by ppeczek on 2014-05-25.
 */
public class NewPassengerEvent extends SimulatorEvent {
    private final String from;
    private final String to;

    public NewPassengerEvent(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
