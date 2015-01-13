package burtis.modules.simulation.exceptions;

public class NoSuchBusStopException extends Exception
{
    private static final long serialVersionUID = -2590241388530897238L;
    private final String busStopName;

    public NoSuchBusStopException(String busStopName)
    {
        super();
        this.busStopName = busStopName;
    }

    public String getBusStopName()
    {
        return busStopName;
    }
    
}
