package model;

/**
 * Created by macdr_000 on 2014-05-26.
 */
public class SimulationTimer
{
    private static SimulationTimer ourInstance = new SimulationTimer();
    private Long currentStep;

    private SimulationTimer()
    {
        currentStep = 0L;
    }

    public static SimulationTimer getInstance()
    {
        return ourInstance;
    }

    public void go()
    {
        currentStep++;
    }

    public Long getTime()
    {
        return new Long(currentStep);
    }
}
