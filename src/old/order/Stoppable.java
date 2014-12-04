package order;

/**
 * Provides method to stop something from running. Functional method is
 * {@link #stop}.
 * 
 * @author Amadeusz Sadowski
 *
 */
@FunctionalInterface
public interface Stoppable
{
    public void stop();
}
