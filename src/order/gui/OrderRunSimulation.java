package order.gui;

import order.FunctionalitySimulationModule;
import order.Order;

/**
 * Implementacja rozkazu używanego przez GUI.
 *
 * @author Maciej Majewski
 */
public class OrderRunSimulation implements Order<FunctionalitySimulationModule> {

    private boolean runSim;


    /**
     * @param shouldRun false, jeśli symulacja ma się zatrzymać
     */
    public OrderRunSimulation(boolean shouldRun) {
        runSim = shouldRun;
    }


    @Override
    public void execute(FunctionalitySimulationModule subject) {
        subject.runSimulation(runSim);
    }

}
