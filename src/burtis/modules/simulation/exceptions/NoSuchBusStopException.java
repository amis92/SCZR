package burtis.modules.simulation.exceptions;

import burtis.modules.simulation.models.BusStop;

public class NoSuchBusStopException extends Exception
{
    private final String busStopName;

    public NoSuchBusStopException(BusStop busStop)
    {
        super();
        this.busStopName = busStop.getName();
    }

    public String getBusStopName()
    {
        return busStopName;
    }
    
}
