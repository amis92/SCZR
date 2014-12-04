package model;

import main.SimulatorConstants;

/**
 * Created by ppeczek on 2014-05-21.
 */
public class BusTerminus extends BusStop {
    private static BusTerminus ourInstance = new BusTerminus();

    private BusTerminus() {
        super(SimulatorConstants.terminusName);
    }

    public static BusTerminus getInstance() {
        return ourInstance;
    }
}
