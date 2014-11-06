package order.zkm;

import order.FunctionalitySimulationModule;
import order.Order;

/**
 * Implementacja rozkazu wydawanego przez zkm.
 * Zastawia sidła na krakena. Albo bus, whatever.
 *
 * @author Maciej Majewski
 */
public class OrderTrapBus implements Order<FunctionalitySimulationModule> {

    @Override
    public void execute(FunctionalitySimulationModule subject) {
        subject.trapBus();
    }

}
