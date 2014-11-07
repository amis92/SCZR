package event.guievents;

import view.SimulatorEvent;

import java.io.Serializable;

/**
 * Created by ppeczek on 2014-05-25.
 */
public class PassengerGenerationInterval extends SimulatorEvent{
    private final int min;
    private final int max;

    public PassengerGenerationInterval(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
