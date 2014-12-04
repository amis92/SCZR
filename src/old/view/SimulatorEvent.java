package view;

import mockup.Mockup;
import model.Bus;

import java.io.Serializable;

/**
 * Created by ppeczek on 2014-05-25.
 */
public class SimulatorEvent implements Serializable
{
    public Bus getBus() {
        return null;
    }

    public String getTo() { return null; }

    public String getFrom() { return null; }

    public int getMin() {
        return 0;
    }

    public int getMax() {
        return 0;
    }

    public boolean isContinuous() {
        return false;
    }

    public Mockup getMockup() { return null; }
}
