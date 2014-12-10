package burtis.modules.sync;

import burtis.common.events.Sync.TickEvent;
import java.util.logging.Level;

/**
* Class implementing thread responsible for sending ticks.
*/
class TickTask implements Runnable
{
    
    SimulationServer sync = SimulationServer.getInstance();

    @Override
    public void run() {

        while (!Thread.interrupted()) {

            // Wait untile we're ready
            while (!sync.isReadyForTick()) {
                if (Thread.interrupted()) {
                    return;
                }
            }

            if (!Thread.interrupted()) {

                sync.noReadyForTick();

                Module.resetModulesStates();

                sync.nextIteration();

                Module.resetModuleWatchdog();

                long iteration = sync.getIteration();
                sync.send(new TickEvent(sync.getModuleConfig().getModuleName(), iteration));
                sync.getLogger().log(Level.INFO, "Simulation step {0}", iteration);

                if (sync.executeOnce()) {
                    break;
                }
            }
        }
    }
}
