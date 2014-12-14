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
    private final ExecutorService handlerExecutor = Executors
            .newSingleThreadExecutor();
    private boolean isRunning = true;
    private BlockingQueue<SimulationEvent> queue;
    protected ClientModule client;
    protected AbstractEventProcessor eventHandler = null;
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
     */
    protected AbstractNetworkModule(ModuleConfig config)
    {
        this.moduleConfig = config;
    }

    public void send(SimulationEvent event)
    {
        client.send(event);
    }

    private boolean checkInputAndSleep()
    {
        try
        {
            Thread.sleep(500);
            return System.in.available() != 0;
        }
        catch (Exception e)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null,
                    e);
        }
        return false;
    }

    protected void closeModule()
    {
        client.close();
        isRunning = false;
        handlerExecutor.shutdownNow();
        terminate();
    }

    private void initializeModule() throws IOException
    {
        client = new ClientModule(moduleConfig);
        queue = client.getIncomingQueue();
        handlerExecutor.execute(this::listenOnClient);
        client.connect();
        client.getIncomingQueue();
        init();
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
     * IMPORTANT: set protected field {@link #eventHandler} before calling this
     * method.
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
        }
        catch (IOException ex)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null,
                    ex);
            return;
        }
        System.out.println("Naciśnij enter any zakończyć.");
        boolean isInputAvailable = false;
        while (!isInputAvailable)
        {
            isInputAvailable = checkInputAndSleep();
        }
        closeModule();
    }

    protected abstract void terminate();
}
