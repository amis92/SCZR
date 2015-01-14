package burtis.modules.gui.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import burtis.modules.gui.View;
import burtis.modules.gui.events.ConnectEvent;
import burtis.modules.gui.events.DisconnectEvent;
import burtis.modules.gui.events.GoEvent;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.events.ShowBusEvent;
import burtis.modules.gui.events.ShowBusStopEvent;
import burtis.modules.gui.events.StepEvent;
import burtis.modules.gui.events.StopEvent;

public class Controller
{
    private final static Logger logger = Logger.getLogger(Controller.class
            .getName());
    private final View view;
    /** Kolejka dla obiektow ProgramEvent. */
    private final BlockingQueue<ProgramEvent> blockingQueue;
    /** odwzorowanie obiektow ProgramEvent na obiekty ProgramAction */
    private final Map<Class<? extends ProgramEvent>, ProgramAction> eventActionMap;
    private boolean isRunning = false;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ActionExecutor actionExecutor;

    /**
     * Tworzy obiekt typu Controller
     * 
     * @param view
     *            referencja na widok
     * @param blockingQueue
     *            kolejka do otrzymywania komunikatow z Widoku
     */
    public Controller(View view, BlockingQueue<ProgramEvent> blockingQueue,
            ActionExecutor actionExecutor)
    {
        this.actionExecutor = actionExecutor;
        this.view = view;
        this.blockingQueue = blockingQueue;
        eventActionMap = new HashMap<Class<? extends ProgramEvent>, ProgramAction>();
        fillEventActionMap();
    }

    /**
     * zapelnia kontener eventActionMap
     */
    private void fillEventActionMap()
    {
        eventActionMap.put(GoEvent.class, e -> actionExecutor.sendStartEvent());
        eventActionMap.put(StepEvent.class,
                e -> actionExecutor.sendOneStepEvent());
        eventActionMap
                .put(StopEvent.class, e -> actionExecutor.sendStopEvent());
        eventActionMap.put(ConnectEvent.class, e ->
        {
            try
            {
                actionExecutor.connect();
                view.setConnectionStatus(true);
            }
            catch (Exception e1)
            {
                view.setConnectionStatus(false);
            }
        });
        eventActionMap.put(DisconnectEvent.class, e ->
        {
            actionExecutor.disconnect();
            view.setConnectionStatus(false);
        });
        eventActionMap.put(ShowBusEvent.class,
                e -> view.updateBusInfoPanel(((ShowBusEvent) e).getId()));
        eventActionMap.put(ShowBusStopEvent.class, e -> view
                .updateBusStopInfoPanel(((ShowBusStopEvent) e).getName()));
    }

    /**
     * Runs new working thread.
     */
    public void start()
    {
        isRunning = true;
        executor = Executors.newSingleThreadExecutor();
        executor.execute(this::work);
    }

    /**
     * Stops currently running thread.
     */
    public void stop()
    {
        isRunning = false;
        executor.shutdownNow();
    }

    /**
     * funkcja obslugujca komunikaty z widoku w nieskonczonej petki <br>
     * tzn pobieajaca obiekt z kolejki(blockingQueue) i na jego podstawie
     * uruchamiajaca odpowiednie dzialanie z mapy zadan(eventActionMap) <br>
     * -normalne dzialanie kontrolera :)
     */
    private void work()
    {
        while (isRunning)
        {
            try
            {
                ProgramEvent event = blockingQueue.take();
                ProgramAction programAction = eventActionMap.get(event
                        .getClass());
                programAction.go(event);
            }
            catch (Exception e)
            {
                logger.log(Level.SEVERE, "Exception in controller loop:", e);
                throw new RuntimeException(e);
            }
        }
    }
}
