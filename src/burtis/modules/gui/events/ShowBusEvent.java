package burtis.modules.gui.events;

public class ShowBusEvent extends ProgramEvent
{
    private Integer i;
    private String currentBusStop = null;
    
    public ShowBusEvent(Integer i, String currentBusStop) {
        this.i = i;
        this.currentBusStop = currentBusStop;
    }

    public Integer getId()
    {
        return i;
    }
}
