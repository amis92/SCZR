package burtis.modules.busscheduler;

import burtis.modules.AbstractNetworkModule;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;

/**
 * Demo class to easily use default BusScheduler on network.
 * 
 * @author Amadeusz Sadowski
 *
 */
public class BusSchedulerModule extends AbstractNetworkModule
{
    public static void main(String[] args)
    {
        ModuleConfig config = NetworkConfig.defaultConfig().getModuleConfigs()
                .get(NetworkConfig.BUSSHED_MODULE);
        BusSchedulerModule schedModule = new BusSchedulerModule(config);
        schedModule.main();
    }

    private final BusScheduler scheduler;

    public BusSchedulerModule(ModuleConfig config)
    {
        super(config);
        scheduler = new BusScheduler(config, this::send, this::shutdown);
        eventHandler = scheduler;
    }

    @Override
    protected void init()
    {
        scheduler.init();
    }

    @Override
    protected void terminate()
    {
        scheduler.terminate();
    }
}
