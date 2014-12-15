package burtis.common.events.Passengers;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.ConfigurationEvent;

/**
 *
 * @author Mikołaj Sowiński
 */
public class PassengerGenerationRateConfigurationEvent extends
        ConfigurationEvent
{
    private static final long serialVersionUID = 1L;
    private final int generationCycleLength;
    private final int passengersPerCycle;

    public PassengerGenerationRateConfigurationEvent(int generationCycleLength,
            int passengersPerCycle, String sender)
    {
        super(sender);
        this.generationCycleLength = generationCycleLength;
        this.passengersPerCycle = passengersPerCycle;
    }

    public int getGenerationCycleLength()
    {
        return generationCycleLength;
    }

    public int getPassengersPerCycle()
    {
        return passengersPerCycle;
    }

    public void visit(AbstractEventHandler eventProcessor)
    {
        eventProcessor.process(this);
    }
}
