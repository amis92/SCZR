package order.gui;

import order.FunctionalitySimulationModule;
import order.Order;

/**
 * Implementacja rozkazu używanego przez GUI
 *
 * @author Maciej Majewski
 */
public class OrderGenerationConfig implements Order<FunctionalitySimulationModule> {

    private final int min;
    private final int max;

    /**
     * @param min najmniejsza liczba kroków przed generacją podróżnego
     * @param max największa liczba kroków od generacji podróżnego
     */
    public OrderGenerationConfig(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public void execute(FunctionalitySimulationModule subject) {
        subject.passengerGenerationConfig(min, max);
    }

}
