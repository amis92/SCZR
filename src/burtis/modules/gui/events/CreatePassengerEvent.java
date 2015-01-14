package burtis.modules.gui.events;

public class CreatePassengerEvent extends ProgramEvent
{
    private final String origin;
    private final String destination;

    public CreatePassengerEvent(String origin, String destination)
    {
        this.origin = origin;
        this.destination = destination;
    }

    public String getOrigin()
    {
        return origin;
    }

    public String getDestination()
    {
        return destination;
    }
}
