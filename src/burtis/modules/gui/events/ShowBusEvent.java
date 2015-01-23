package burtis.modules.gui.events;

public class ShowBusEvent extends ProgramEvent
{
    private final Integer i;

    public ShowBusEvent(Integer i)
    {
        this.i = i;
    }

    public Integer getId()
    {
        return i;
    }
}
