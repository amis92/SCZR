package exceptions;

/**
 * Created by ppeczek on 2014-05-24.
 */
public class CounterNotInitiatedException extends Exception {
    public CounterNotInitiatedException() {
        super("Counter was not initiated!");
    }
}
