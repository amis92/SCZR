package burtis.common.events;

public class ErrorEvent extends SimulationEvent
{
    String message;
    
    public ErrorEvent(String sender, String message)
    {
        super(sender);
        this.message = message;
    }
    
    public String getMessage() { return message; }
}
