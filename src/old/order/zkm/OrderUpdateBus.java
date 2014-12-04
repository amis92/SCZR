package order.zkm;

import order.FunctionalitySimulationModule;
import order.Order;

/**
 * Implementacja rozkazu wydawanego przez zkm.
 * Odświeża krakena dezodorantem. Bo tak.
 *
 * @author Maciej Majewski
 */
public class OrderUpdateBus implements Order<FunctionalitySimulationModule> {

    @Override
    public void execute(FunctionalitySimulationModule subject) {
        subject.updateBus();
    }

}
