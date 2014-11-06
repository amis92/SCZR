package event.guievents;

import view.SimulatorEvent;

import java.io.Serializable;

/**
 * Created by ppeczek on 2014-05-25.
 */
public class ContinuousSimulationEvent extends SimulatorEvent{
    private final boolean continuous;

    public ContinuousSimulationEvent(boolean continuous) {
        this.continuous = continuous;
    }

    public boolean isContinuous() {
        return continuous;
    }
}
