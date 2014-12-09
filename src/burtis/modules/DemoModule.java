package burtis.modules;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.SimulationEvent;
import burtis.common.events.Sync.TickEvent;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;

/**
 * Demoes how modules should be built using {@link AbstractNetworkModule} and
 * {@link AbstractEventProcessor} templates.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class DemoModule extends AbstractNetworkModule
{
    private DemoModule(ModuleConfig config)
    {
        super(config);
    }

    @Override
    protected void init()
    {
        System.out.println("init() called. Initializing resources.");
    }

    @Override
    protected void terminate()
    {
        System.out
                .println("terminate() called. Closing resources, stopping threads.");
    }

    public static void main(String[] args)
    {
        ModuleConfig config = NetworkConfig.defaultConfig().getModuleConfigs()
                .get(NetworkConfig.GUI_MODULE);
        DemoModule app = new DemoModule(config);
        app.eventHandler = app.new EventHandler();
        app.main();
    }

    private class EventHandler extends AbstractEventProcessor
    {
        @Override
        public void defaultHandle(SimulationEvent event)
        {
            System.out.printf("defaultHandle called with {0}\n", event);
        }

        @Override
        public void process(TickEvent event)
        {
            System.out.printf(
                    "my overriden process(TickEvent) called with {0}\n", event);
            super.process(event);
        }
    }
}
