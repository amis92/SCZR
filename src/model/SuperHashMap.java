package model;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by ppeczek on 2014-05-29.
 */
public class SuperHashMap extends HashMap<BusStop, LinkedList<Passenger>> {

    public void put(BusStop busStop, Passenger passenger) {
        if (!containsKey(busStop)) {
            super.put(busStop, new LinkedList<Passenger>());
        }
        get(busStop).add(passenger);
    }

    public Passenger poll(BusStop busStop) {
        if (get(busStop) != null) {
            Passenger passenger = get(busStop).poll();
            if (get(busStop).isEmpty()) {
                remove(busStop);
            }
            return passenger;
        }
        return null;
    }

    @Override
    public int size() {
        int superSize = 0;
        for (LinkedList<Passenger> linkedList: values()) {
            superSize += linkedList.size();
        }
        return superSize;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }
}
