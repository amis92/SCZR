package burtis.modules.passengers.exceptions;

/**
 * Thrown when desired bus stop couldn't be found.
 * 
 * @author Mikołaj Sowiński
 *
 */
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
