package order.gui;

import order.FunctionalitySimulationModule;
import order.Order;

/**
 * Implementacja rozkazu używanego przez GUI.
 *
 * @author Maciej Majewski
 */
public class OrderCreatePassenger implements Order<FunctionalitySimulationModule> {

    private final String from;
    private final String to;

    public OrderCreatePassenger(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void execute(FunctionalitySimulationModule subject) {
        subject.newPassenger(from, to);
    }

}
