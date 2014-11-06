package model;

import main.SimulatorConstants;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by ppeczek on 2014-05-21.
 */
public class BusDepot extends BusStop {
    private static BusDepot ourInstance = new BusDepot();
    private Queue<Bus> busQueue = new LinkedList<Bus>();

    private BusDepot() {
        super(SimulatorConstants.depotName);
    }

    public static BusDepot getInstance() {
        return ourInstance;
    }

    public void setRoute(int length) {
        setRoute(BusTerminus.getInstance(), length);
    }

    public Queue<Bus> getBusQueue() {
        return busQueue;
    }
}
