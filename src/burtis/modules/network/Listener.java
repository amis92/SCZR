package burtis.modules.network;

/**
 * Listens for incoming traffic. Functional method is {@link #listen()}.
 * 
 * @author Amadeusz Sadowski
 *
 */
@FunctionalInterface
public interface Listener
{
    public void listen();
}
