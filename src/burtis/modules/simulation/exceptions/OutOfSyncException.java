package burtis.modules.simulation.exceptions;

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
