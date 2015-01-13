package burtis.common.events.Simulator;

import burtis.common.events.AbstractEventHandler;
import burtis.common.events.SimulationEvent;
import burtis.common.mockups.MockupBus;
import burtis.modules.network.NetworkConfig;

import java.util.List;

/**
 * Event transferring states of buses from simulation module to the passenger 
 * module.
 * 
 * @author Mikołaj Sowiński
 */
public class BusMockupsEvent extends SimulationEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final List<MockupBus> busMockups;
    private final long iteration;

    public BusMockupsEvent(String sender, List<MockupBus> busMockups, long iteration) {
        super(sender, new String[] {NetworkConfig.getModuleName(NetworkConfig.PSNGR_MODULE)});
        this.busMockups = busMockups;
        this.iteration = iteration;
    }

    public List<MockupBus> getBusMockups() {
        return busMockups;
    }

    public long iteration() {
        return iteration;
    }
   
    @Override
    public void visit(AbstractEventHandler eventProcessor) {
        eventProcessor.process(this);
    }
    
}
