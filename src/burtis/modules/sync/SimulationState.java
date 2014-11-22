package burtis.modules.sync;

import java.util.concurrent.BlockingQueue;

import burtis.common.events.ChangeSimulationModeEvent.State;
import burtis.common.events.SimulationEvent;
import burtis.modules.network.client.Client;

public abstract class SimulationState
{
    BlockingQueue<SimulationEvent> clientBlockingQueue;
    Client<SimulationEvent> client;
    
    public SimulationState(BlockingQueue<SimulationEvent> clientBlockingQueue, Client<SimulationEvent> client) {
        this.clientBlockingQueue = clientBlockingQueue;
        this.client = client;
    }
    
    public abstract void run();
    public abstract State id();
}
