package burtis.modules.simulation;

import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.constants.SimulationModuleConsts;
import burtis.modules.AbstractNetworkModule;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;
import burtis.modules.simulation.models.Bus;
import burtis.modules.simulation.models.BusStop;
import burtis.modules.simulation.models.Terminus;

/**
 * Simulation module.
 * 
 * Contains depot, terminus, buses and bus stops. No passengers here. Communicates
 * with passenger module to obtain information on passengers waiting at bus stops.
 * 
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 *
 */
public class Simulation extends AbstractNetworkModule
{
    
    private final static Simulation simulation = new Simulation(NetworkConfig.defaultConfig().getModuleConfigs().get(NetworkConfig.SIM_MODULE));

    private Logger logger;
    
    /**
     * Current cycle.
     * Positive value mens in cycle, negative in between.
     */
    protected static long currentCycle;
    
    private int lineLength = 0;

    public int getLineLength() {
        return lineLength;
    }
    
    
    
    public Logger getLogger() {
        return logger;
    }
    
    public static Simulation getInstance() {
        return simulation;
    }
    
    public ModuleConfig getModuleConfig() {
        return moduleConfig;
    }
    
    /**
     * Creates a new simulation.
     */
    private Simulation(ModuleConfig config) {
        super(config); 
        logger = Logger.getLogger(moduleConfig.getModuleName());
    }
 
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Simulation app = Simulation.getInstance();
        app.eventHandler = new SimulationEventHandler();
        app.main();
    }

    @Override
    protected void init() {
        
        for(int i=0; i< SimulationModuleConsts.NUMBER_OF_BUSES; i++) {
            Bus.add(SimulationModuleConsts.BUS_CAPACITY);
        }
        
        // Create bus stops
        BusStop.add(new Terminus(0,"Bielańska"));
        BusStop.add(30, "Plac Zamkowy");
        BusStop.add(60, "Hotel Bristol");
        BusStop.add(90, "Uniwersytet");
        BusStop.add(120, "Ordynacka");
        BusStop.add(150, "Foksal");
        BusStop.add(180, "Plac Trzech Krzyży");
        BusStop.add(210, "Plac na Rozdrożu");
        BusStop.add(240, "Plac Unii Lubelskiej");
        BusStop.add(270, "Rakowiecka");
        BusStop.add(new Terminus(300,"Bielańska"));
        
        lineLength = 300;
        
    }

    @Override
    protected void terminate() {
        logger.log(Level.INFO, "Terminating module...");
    }

   

}