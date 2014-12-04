package main;

import controller.Controller;

/**
 * <b>Simulator</b><br>
 *     Main class.
 */
public final class Simulator {

    public static void main(final String[] args) throws Exception {
        final Controller controller = Controller.getInstance();
        controller.start();
    }

}
