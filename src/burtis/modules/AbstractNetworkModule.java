package burtis.modules;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.events.AbstractEventProcessor;
import burtis.common.events.SimulationEvent;
import burtis.modules.network.ModuleConfig;
import burtis.modules.network.client.ClientModule;

/**
 * Template class for creating network-connected modules.
 * 
 * It provides main method, so in your main method you can just write:</br>
 * <code>
 * MyImplementation app = new MyImplementation();</br>
 * app.main();</br>
 * </code>
 * 
 * @author Amadeusz Sadowski
 *
 */
public abstract class AbstractNetworkModule
{
    private ClientModule client;
    private final ExecutorService handlerExecutor = Executors
            .newSingleThreadExecutor();
    private boolean isRunning = true;
    private BlockingQueue<SimulationEvent> queue;
    protected final AbstractEventProcessor eventHandler;
    /**
     * Must return configuration for your implementation, even before
     * {@link #init()} call.
     * 
     * @return
     */
    protected final ModuleConfig moduleConfig;

    /**
     * Creates module ready to have {@link #main()} called.
     * 
     * @param config
     * @param eventHandler
     */
    protected AbstractNetworkModule(ModuleConfig config,
            AbstractEventProcessor eventHandler)
    {
        this.moduleConfig = config;
        this.eventHandler = eventHandler;
    }

    private void closeModule()
    {
        client.close();
        isRunning = false;
        handlerExecutor.shutdownNow();
    }

    private void initializeModule() throws IOException
    {
        client = new ClientModule(moduleConfig);
        queue = client.getIncomingQueue();
        handlerExecutor.execute(this::listenOnClient);
        client.connect();
        client.getIncomingQueue();
    }

    private void listenOnClient()
    {
        while (isRunning)
        {
            try
            {
                SimulationEvent event = queue.take();
                event.visit(eventHandler);
            }
            catch (InterruptedException e)
            {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                        null, e);
            }
        }
    }

    protected abstract void init();

    /**
     * 
     * It provides main method, so in your main method you can just write:</br>
     * <code>
     * MyImplementation app = new MyImplementation();</br>
     * app.main();</br>
     * </code>
     * 
     * It first initializes this base class, then calls {@link #init()}. After
     * console gets any input, {@link #terminate()} is called, after which this
     * base class terminates itself and main returns.
     */
    protected void main()
    {
        try
        {
            initializeModule();
            init();
            System.out.println("Naciśnij enter any zakończyć.");
            System.in.read();
        }
        catch (IOException ex)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null,
                    ex);
        }
        closeModule();
        terminate();
    }

    protected void send(SimulationEvent event)
    {
        client.send(event);
    }

    protected abstract void terminate();
}
