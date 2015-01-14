package burtis.modules.gui.events;

public class PassengerGenRateEvent extends ProgramEvent
{
    private final int gcl;
    private final int ppc;

    public PassengerGenRateEvent(int gcl, int ppc)
    {
        this.gcl = gcl;
        this.ppc = ppc;
    }

    public int getGcl()
    {
        return gcl;
    }

    public int getPpc()
    {
        return ppc;
    }
}
