package burtis.modules.simulation.exceptions;

public class OutOfSyncException extends Exception
{
    private final String callingClass;
    
    public OutOfSyncException(String callingClass)
    {
        this.callingClass = callingClass;
    }
    
    
}
