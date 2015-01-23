package burtis.modules.sync;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.common.events.SimulationEvent;
import burtis.common.events.flow.TickEvent;

/**
 * Controls thread responsible for sending ticks.
 * 
 * 
 * @author Amadeusz Sadowski
 * @author Mikołaj Sowiński
 */
class TickService
{
    private static final Logger logger = Logger.getLogger(TickService.class
            .getName());
    private ScheduledExecutorService executor = Executors
            .newSingleThreadScheduledExecutor();
    private final LongSupplier iterationSupplier;
    private final Consumer<SimulationEvent> sender;
    private final String senderName;
    private final WatchdogService watchdogService;

    public TickService(String senderName, LongSupplier iterationSupplier,
            Consumer<SimulationEvent> sender, WatchdogService watchdogService)
    {
        this.iterationSupplier = iterationSupplier;
        this.sender = sender;
        this.senderName = senderName;
        this.watchdogService = watchdogService;
    }

    /**
     * Starts sending ticks at a fixed rate.
     * 
     * @param period
     *            - time between ticks.
     */
    public void start(long period)
    {
        stop();
        logger.info("Ticking started.");
        executor.scheduleAtFixedRate(this::tick, 0, period,
                TimeUnit.MILLISECONDS);
    }

    /**
     * Immediately halts ticking thread.
     */
    public void stop()
    {
        executor.shutdownNow();
        executor = Executors.newSingleThreadScheduledExecutor();
        logger.info("Ticking stopped.");
    }

    /**
     * Stops scheduled execution and ticks once.
     */
    public void tickOnce()
    {
        stop();
        tick();
    }

    private void tick()
    {
        watchdogService.acceptTick();
        long iteration = iterationSupplier.getAsLong();
        sender.accept(new TickEvent(senderName, iteration));
        logger.log(Level.INFO, "Sent tick, iteration: {0}", iteration);
    }
}
