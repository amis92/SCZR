package burtis.modules.simulation.exceptions;

/**
 * Thrown when internal interation isn't synchronized with network flow.
 * 
 * @author Mikołaj Sowiński
 *
 */
public class OutOfSyncException extends Exception
{
    private static final long serialVersionUID = -7547301557190574202L;
    private final String callingClass;

    public OutOfSyncException(String callingClass)
    {
        this.callingClass = callingClass;
    }

    public String getCallingClass()
    {
        return callingClass;
    }
}
