package burtis.modules.passengers;

import burtis.common.events.simulation.BusStopsListRequestEvent;
import burtis.modules.AbstractNetworkModule;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.NetworkConfig;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Module responsible for all passengers operations.
 * 
 * @author Mikołaj Sowiński
 */
public class PassengerModule extends AbstractNetworkModule {
    
    private static final PassengerModule pm = new PassengerModule(NetworkConfig.defaultConfig().getModuleConfigs().get(NetworkConfig.PSNGR_MODULE));
    
    private Logger logger;
    
    /**
     * Current cycle.
     * Positive value mens in cycle, negative in between.
     */
    protected static long currentCycle;
    
    /**
     * State of the module.
     * Init is the state before receiving a bus stop list from simulation module.
     */
    public enum State {
        INIT, RUNNING
    }
    protected static State state = State.INIT;

    protected static State getState() {
        return state;
    }
         
    public static PassengerModule getInstance() {
        return pm;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public ModuleConfig getModuleConfig() {
        return moduleConfig;
    }
   
    private PassengerModule(ModuleConfig config) {
        super(config);
        logger = Logger.getLogger(config.getModuleName());
    }
    
    @Override
    protected void init() {
        logger.log(Level.INFO, "Requesting bus stops list...(timeout 1min)");
        send(new BusStopsListRequestEvent());
    }

    @Override
    protected void terminate() {
        logger.log(Level.INFO, "Terminating module...");
        // TODO call close
    }

    public static void main(String[] args) {
        PassengerModule pm = PassengerModule.getInstance();
        pm.eventHandler = new PassengerModuleEventHandler();
        pm.main();
    }
    
}
