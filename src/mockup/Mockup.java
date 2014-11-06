package mockup;

import model.Bus;
import model.BusStop;
import model.Schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>Mockup</b><br>
 * Makieta.<br>
 * <br>
 * Zawiera <b>rozkład jazdy</b> ( listę <b>Przystanków</b> ) oraz listę <b>Autobusów</b>.
 */
public final class Mockup implements Serializable
{
    private ArrayList<MockupBus> schedule = new ArrayList<MockupBus>();
    private ArrayList<MockupBusStop> busStops = new ArrayList<MockupBusStop>();
    private final long currentTime;
    private final int minPassengerGenerationTime;
    private final int maxPassengerGenerationTime;

    public Mockup(final ArrayList<Bus> buses, final Schedule schedule, long currentTime) {
        for (Bus b: buses) {
            this.schedule.add(new MockupBus(b));
        }
        for (BusStop bs: schedule.getBusStops()) {
            this.busStops.add(new MockupBusStop(bs));
        }
        this.currentTime = currentTime;
        this.minPassengerGenerationTime = schedule.getMinPassengerGenerationValue();
        this.maxPassengerGenerationTime = schedule.getMaxPassengerGenerationValue();
    }

    /**
     * @return schedule
     */
    public List<MockupBus> getBuses() {
        return schedule;
    }

    /**
     * @return busStops
     */
    public List<MockupBusStop> getBusStops() {
        return busStops;
    }

    public long getCurrentTime()
    {
        return currentTime;
    }
}