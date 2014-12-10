package burtis.modules.gui.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import burtis.common.events.SimulationEvent;
import burtis.modules.gui.events.ConnectEvent;
import burtis.modules.gui.events.DisconnectEvent;
import burtis.modules.gui.events.GoEvent;
import burtis.modules.gui.events.ProgramEvent;
import burtis.modules.gui.events.ShowBusEvent;
import burtis.modules.gui.events.ShowBusStopEvent;
import burtis.modules.gui.events.StepEvent;
import burtis.modules.gui.events.StopEvent;
import burtis.modules.gui.view.View;

public class Controller
{
    private final View view;
    /** Kolejka dla obiektow ProgramEvent. */
    private final BlockingQueue<ProgramEvent> blockingQueue;
    /** odwzorowanie obiektow ProgramEvent na obiekty ProgramAction */
    private final Map<Class<? extends ProgramEvent>, ProgramAction> eventActionMap;
    private final Consumer<SimulationEvent> sender;
    private boolean isRunning = false;
    private ExecutorService executor = Executors
            .newSingleThreadExecutor();

    /**
     * Tworzy obiekt typu Controller
     * 
     * @param view
     *            referencja na widok
     * @param model
     *            referencja na Model
     * @param blockingQueue
     *            kolejka do otrzymywania komunikatow z Widoku
     */
    public Controller(View view, BlockingQueue<ProgramEvent> blockingQueue,
            Consumer<SimulationEvent> sender)
    {
        this.view = view;
        this.blockingQueue = blockingQueue;
        this.sender = sender;
        eventActionMap = new HashMap<Class<? extends ProgramEvent>, ProgramAction>();
        fillEventActionMap();
    }

    /**
     * zapelnia kontener eventActionMap
     */
    private void fillEventActionMap()
    {
        eventActionMap.put(GoEvent.class, new ProgramAction() {
            public void go(ProgramEvent e)
            {
                // sender.accept(new GoSimulationEvent());
                System.out.println("Go");
            }
        });
        eventActionMap.put(StepEvent.class, new ProgramAction() {
            public void go(ProgramEvent e)
            {
                System.out.println("Step");
            }
        });
        eventActionMap.put(StopEvent.class, new ProgramAction() {
            public void go(ProgramEvent e)
            {
                System.out.println("Stop");
            }
        });
        eventActionMap.put(ConnectEvent.class, new ProgramAction() {
            public void go(ProgramEvent e)
            {
                System.out.println("Connect");
            }
        });
        eventActionMap.put(DisconnectEvent.class,
                e -> System.out.println("Disconnect"));
        eventActionMap.put(ShowBusEvent.class, new ProgramAction() {
            public void go(ProgramEvent e)
            {
                view.updateBusInfoPanel(((ShowBusEvent) e).getId());
                // System.out.println("Show Bus" + ((ShowBusEvent)e).getId());
            }
        });
        eventActionMap.put(ShowBusStopEvent.class, new ProgramAction() {
            public void go(ProgramEvent e)
            {
                // System.out.println("Show Bus Stop");
                view.updateBusStopInfoPanel(((ShowBusStopEvent) e).getName());
            }
        });
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
     * uruchamiajaca odpowiednie dzialannie z mapy zadan(eventActionMap) <br>
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
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
