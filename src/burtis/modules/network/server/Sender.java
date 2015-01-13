package burtis.modules.network.server;

/**
 * Allows for unblocked sending of objects to other {@link ModuleConnection}s.
 * Functional method is {@link #send}
 * 
 * @author Amadeusz Sadowski
 *
 */
@FunctionalInterface
public interface Sender
{
    public void send(final Object object, final ModuleConnection recipient);
}
