package event;

import view.SimulatorEvent;

/**
 * Created by ppeczek on 2014-05-25.
 */
public class BusReleasingFrequency extends SimulatorEvent {
    private final int frequency;

    public BusReleasingFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }
}
