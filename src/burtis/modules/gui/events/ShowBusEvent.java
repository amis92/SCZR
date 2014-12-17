package burtis.modules.gui.events;

public class ShowBusEvent extends ProgramEvent
{
    private Integer i;

    public ShowBusEvent(Integer i)
    {
        this.i = i;
    }

    public Integer getId()
    {
        return i;
    }
}
