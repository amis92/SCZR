package burtis.common.mockups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Mockup implements Serializable
{
    private static final long serialVersionUID = 1L;
    private ArrayList<MockupBus> schedule = new ArrayList<MockupBus>();
    private ArrayList<MockupBusStop> busStops = new ArrayList<MockupBusStop>();
    private long currentTime;
    private int minPassengerGenerationTime;
    private int maxPassengerGenerationTime;

    public Mockup(final ArrayList<MockupBus> mockupBusArray,
            final ArrayList<MockupBusStop> mockupBusStopArray, long currentTime)
    {
        for (MockupBus b : mockupBusArray)
        {
            this.schedule.add(b);
        }
        for (MockupBusStop bs : mockupBusStopArray)
        {
            this.busStops.add(bs);
        }
        this.currentTime = currentTime;
        // this.minPassengerGenerationTime =
        // schedule.getMinPassengerGenerationValue();
        // this.maxPassengerGenerationTime =
        // schedule.getMinPassengerGenerationValue();
    }

    /**
     * @return schedule
     */
    public List<MockupBus> getBuses()
    {
        return schedule;
    }

    /**
     * @return busStops
     */
    public List<MockupBusStop> getBusStops()
    {
        return busStops;
    }

    public long getCurrentTime()
    {
        return currentTime;
    }
}
