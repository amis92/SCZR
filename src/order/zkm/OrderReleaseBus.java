package order.zkm;

import order.FunctionalitySimulationModule;
import order.Order;

/**
 * Implementacja rozkazu wydawanego przez zkm.
 * Wypuszcza krakena. Albo bus, whatever.
 *
 * @author Maciej Majewski
 */
public class OrderReleaseBus implements Order<FunctionalitySimulationModule> {

    @Override
    public void execute(FunctionalitySimulationModule subject) {
        subject.releaseBus();
    }

}
