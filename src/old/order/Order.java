package order;

/**
 * Provides method to be executed on some object. Functional method is
 * {@link #execute(Object)}. Recipient of the order should execute it on itself
 * in most cases.
 * 
 * Implementation of order for object of class SomeClass to execute method
 * someMethod would look like so:
 * 
 * <pre>
 * <code>
 * public void execute(SomeClass subject) {
 *      subject.someMethod();
 * }
 * </code>
 * </pre>
 * 
 * @author Amadeusz Sadowski
 *
 * @param <T>
 *            Type of object being target of order (argument of function).
 */
@FunctionalInterface
public interface Order<T>
{
    public void execute(T subject);
}
