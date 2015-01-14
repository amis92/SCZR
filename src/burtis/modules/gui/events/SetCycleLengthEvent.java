package burtis.modules.gui.events;

public class SetCycleLengthEvent extends ProgramEvent
{
    private final long newCycleLength;

    public SetCycleLengthEvent(long newCycleLength)
    {
        this.newCycleLength = newCycleLength;
    }

    public long getNewCycleLength()
    {
        return newCycleLength;
    }
}
