package burtis.modules.network.server;

/**
 * Allows for execution of some action. Functional method: {@link #perform}.
 * 
 * @author Amadeusz Sadowski
 *
 */
@FunctionalInterface
public interface Action
{
    public void perform();
}
