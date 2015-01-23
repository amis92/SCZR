package burtis.modules.passengers.exceptions;

/**
 * Thrown when desired bus couldn't be found.
 * 
 * @author Mikołaj Sowiński
 *
 */
public class NoSuchBusException extends Exception
{
    private static final long serialVersionUID = -2590241388530897238L;
    private final int busId;

    public NoSuchBusException(int busId)
    {
        super();
        this.busId = busId;
    }

    public int getBusId()
    {
        return busId;
    }
}
