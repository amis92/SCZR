package controller;

import event.BusReleasingFrequency;
import event.BusStartSignal;
import event.busevents.*;
import event.guievents.ContinuousSimulationEvent;
import event.guievents.NewPassengerEvent;
import event.guievents.PassengerGenerationInterval;
import main.SimulatorConstants;
import mockup.Mockup;
import model.*;
import network.Client;
import view.SimulatorEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * Created by ppeczek on 2014-05-21.
 */
public class Controller  extends  Thread implements ActionListener {
    private static Controller ourInstance = new Controller();

    private final Model model;

    private final LinkedBlockingQueue<SimulatorEvent> eventsBlockingQueue;
    private final Timer timer;
    private Mockup mockup;
    private Client<SimulatorEvent> networkClient;
    private String host;
    private int port;
    private Map<Class<? extends SimulatorEvent>, MyStrategy> resultMap=null;

    private Controller() {
        setNetData(SimulatorConstants.simulatorHostAddress, SimulatorConstants.simulatorPort);
        this.eventsBlockingQueue = new LinkedBlockingQueue<SimulatorEvent>();
        this.model = new Model(eventsBlockingQueue);
        this.mockup = createMockup();
//        this.view = new View(eventsBlockingQueue, mockup);
        this.timer = new Timer(SimulatorConstants.simulationSpeed, this);
        timer.start();
        networkClient = new Client<SimulatorEvent>(host, port);
        networkClient.connect();
        networkClient.setEventsBlockingQueue(eventsBlockingQueue);
    }

    public static Controller getInstance() {
        return ourInstance;
    }

    private Map<Class<? extends SimulatorEvent>, MyStrategy> getEventDictionaryMap() {
        if(resultMap==null) {
            resultMap = new HashMap<Class<? extends SimulatorEvent>, MyStrategy>();
            /**
             * Zdarzenia generowane cyklicznie przez symulator
             */
            resultMap.put(BusStartSignal.class, new BusStartSignalStrategy());
            /**
             * Zdarzenia generowane przez autobus
             */
            resultMap.put(BusArrivesToBusStop.class, new BusArrivesToBusStopStrategy());
            resultMap.put(BusComeBackSignal.class, new BusComeBackSignalStrategy());
            resultMap.put(BusPutOutPassengers.class, new BusPutOutPassengersStrategy());
            resultMap.put(BusPutOutAll.class, new BusPutOutAllStrategy());
            resultMap.put(BusReadyToGo.class, new BusReadyToGoStrategy());
            resultMap.put(BusReturnedToDepot.class, new BusReturnedToDepotStrategy());
            resultMap.put(BusTookInPassengers.class, new BusTookInPassengersStrategy());
            /**
             * Rozkazy użytkownika
             */
            resultMap.put(NewPassengerEvent.class, new NewPassengerStrategy());
            resultMap.put(PassengerGenerationInterval.class, new PassengerGenerationIntervalStrategy());
            resultMap.put(ContinuousSimulationEvent.class, new ContinousSimulationStrategy());
            resultMap.put(BusReleasingFrequency.class, new BusReleasingFrequencyStrategy());
            resultMap = Collections.unmodifiableMap(resultMap);
        }
        return resultMap;
    }

    public Mockup createMockup() {
        return model.createMockup();
    }

    @Override
    public void run() {
        try {
            while (true) {
                SimulatorEvent simulatorEvent = null;
                simulatorEvent = eventsBlockingQueue.take();
//                System.out.println(simulatorEvent.getClass());
                final MyStrategy myStrategy = getEventDictionaryMap().get(simulatorEvent.getClass());
                myStrategy.execute(simulatorEvent);
            }
        } catch (final InterruptedException e) {
            System.out.printf("Controller died.");
        }
    }

    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        try {
            model.step();
        } catch (Exception e) {
            e.printStackTrace();
        }
        networkClient.send(createMockup());
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                setView();
            }
        });
    }

    public void setView() {
//        view.repaint();
    }

    public void setNetData(String host, int port) {
        this.host = host;
        this.port = port;
    }

    abstract private class MyStrategy {
        abstract void execute(SimulatorEvent simulatorEvent);
    }

    /**
     * <b>Obsługa sygnału rozpoczęcia kursu.</b>
     * Autobus zaczyna jechać.
     */
    private final class BusStartSignalStrategy extends MyStrategy {
        @Override
        void execute(SimulatorEvent simulatorEvent) {
            BusDepot busDepot = BusDepot.getInstance();
            Bus bus = busDepot.getBusQueue().poll();
            if (bus != null) {
                bus.setState(BusState.RUNNING);
            }
        }
    }

    /**
     * <b>Obsługa zdarzenia dojazdu autobusu na przystanek.</b>
     * Autobus sprawdza, czy przystanek nie jest zajęty.
     * Jeśli autobus dojechał do pętli, to sprawdza, czy nie kończy kursu.
     * Jeśli nie dojechał do pętli i są pasażerowie, którzy chcą wysiąść, to wysiadają.
     * Jeśli takich nie ma, to wsiadają ci, którzy chcą wsiąść.
     * Jeśli nie ma ani jednych, ani drugich, to autobus jedzie dalej.
     */
    private final class BusArrivesToBusStopStrategy extends MyStrategy {

        @Override
        void execute(SimulatorEvent busEvent) {
            Bus bus = busEvent.getBus();
            bus.terminusCheck();
//            System.out.println(bus.isFinishedLoops());
            if (bus.isFinishedLoops()) {
                if (bus.isEmpty()) {
                    System.out.println(bus + " nie zatrzymuje się na: " + bus.getCurrentBusStop().getNAME());
//                    bus.reachNextStop();
//                    bus.setCurrentBusStop(bus.ge);
                    bus.setState(BusState.FINISHED);
                    bus.comeback();
                } else {
                    if (bus.isNextStopOccupied()) {
                        System.out.println(bus + " czeka na " + bus.getCurrentBusStop().getNAME());
                        bus.setState(BusState.WAITING);
                    } else {
                        System.out.println(bus + " wyrzuca wszystkich na: " + bus.getCurrentBusStop().getNAME());
                        bus.setState(BusState.PUT_OUT_ALL);
                        bus.occupyCurrentBusStop();
                    }
                }
            } else {
                /**
                 * Jeśli autobus jest pełny i nikt na danym przystanku nie wysiada, przystanek jest pomijany.
                 */
                if (bus.isFull() && !bus.isGetOffRequestNow()) {
//                    System.out.println(bus + " nie zatrzymuje się na: " + bus.getCurrentBusStop().getNAME());
                    bus.reachNextStop();
                }
                /**
                 * Jeśli nikt na danym przystanku nie czeka i nie chce wysiadać, przystanek jest pomijany.
                 */
                else if (bus.isEmpty() && !bus.isGetOnRequestNow()) {
//                    System.out.println(bus + " nie zatrzymuje się na: " + bus.getCurrentBusStop().getNAME());
                    bus.reachNextStop();
                } else {
                    if (bus.isNextStopOccupied()) {
                        bus.setState(BusState.WAITING);
                    } else {
//                        System.out.println(bus + ": " + bus.getCurrentBusStop().getNAME());
                        /**
                         * Po zatrzymaniu się autobusu na przystanku najpierw opuszczają go pasażerowie,
                         * dla których jest to przystanek docelowy, a następnie wsiadają do niego
                         * kolejne osoby w miarę wolnych miejsc.
                         */
                        if (bus.isGetOffRequestNow()) {
                            bus.setState(BusState.PUT_OUT);
                            bus.occupyCurrentBusStop();
                        } else if (bus.isGetOnRequestNow()) {
                            bus.setState(BusState.TAKE_IN);
                            bus.occupyCurrentBusStop();
                        }
                    }
                }
//                System.out.println(bus + " " + bus.getState());
            }
        }
    }

    /**
     * <b>Obsługa sygnału powrotu do zajezdni.</b>
     * Jeśli autobus ma pasażerów, to ich wszystkich wypuszcza.
     * W przeciwnym wypadku wraca do zajezdni.
     */
    private final class BusComeBackSignalStrategy extends MyStrategy {
        @Override
        void execute(SimulatorEvent busEvent) {
//            System.out.println(this.getClass());
            Bus bus = busEvent.getBus();
            bus.setFinishedLoops(true);
        }
    }

    /**
     * <b>Obsługa zdarzenia zakończenia wysadzania wszystkich pasażerów z autobusu.</b>
     * Autobus wraca do zajezdni.
     */
    private final class BusPutOutAllStrategy extends MyStrategy {
        @Override
        void execute(SimulatorEvent busEvent) {
//            System.out.println(this.getClass());
            Bus bus = busEvent.getBus();
            bus.setState(BusState.FINISHED);
            bus.comeback();
            bus.freeCurrentBusStop();
//            System.out.println("Następny:" + bus.getToNextStop().getValue());
        }
    }

    /**
     * <b>Obsługa zdarzenia zakończenia wysadzania pasażerów z autobusu.</b>
     * Jeśli jacyś pasażerowie chcą wsiąść, to autobus ich zabiera.
     * W przeciwnym wypadku autobus wyrusza dalej.
     */
    private final class BusPutOutPassengersStrategy extends MyStrategy {
        @Override
        void execute(SimulatorEvent busEvent) {
            Bus bus = busEvent.getBus();
//            System.out.println(this.getClass());
            if (bus.isGetOnRequestNow()) {
                bus.setState(BusState.TAKE_IN);
            }
            else {
                bus.setState(BusState.RUNNING);
                bus.freeCurrentBusStop();
            }
        }
    }

    /**
     * <b>Obsługa zdarzenia gotowości do jazdy.</b>
     * Autobus jest gotowy do jazdy.
     */
    private final class BusReadyToGoStrategy extends MyStrategy {
        @Override
        void execute(SimulatorEvent busEvent) {
//            System.out.println(this.getClass());
            Bus bus = busEvent.getBus();
            BusDepot.getInstance().getBusQueue().add(bus);
            bus.setState(BusState.READY_TO_GO);
            bus.setFinishedLoops(false);
        }
    }

    /**
     * <b>Obsługa zdarzenia powrotu do zajezdni.</b>
     * Autobus, który wrócił do zajezdni ma przerwę.
     * TODO: niewyjebane
     */
    private final class BusReturnedToDepotStrategy extends MyStrategy {
        @Override
        void execute(SimulatorEvent busEvent) {
            Bus bus = busEvent.getBus();
            BusDepot busDepot = BusDepot.getInstance();
            busDepot.getBusQueue().add(bus);
            bus.reachDepot();
            bus.setState(BusState.HAVING_BREAK);
            System.out.println(bus + "returned to depot" + bus.getCurrentBusStop().getNAME());
//            System.out.println(bus + ": " + bus.getCurrentBusStop().getNAME());
        }
    }

    /**
     * <b>Obsługa zdarzenia zabrania wszystkich pasażerów z przystanku.</b>
     * Autobus jedzie dalej.
     */
    private final class BusTookInPassengersStrategy extends MyStrategy {
        @Override
        void execute(SimulatorEvent busEvent) {
            Bus bus = busEvent.getBus();
            bus.setState(BusState.RUNNING);
            bus.freeCurrentBusStop();
        }
    }

    private final class NewPassengerStrategy extends MyStrategy {
        @Override
        void execute(SimulatorEvent simulatorEvent) {
            BusStop from = Schedule.getInstance().findBusStop(simulatorEvent.getFrom());
            BusStop to = Schedule.getInstance().findBusStop(simulatorEvent.getTo());
            if(from != null && to != null) {
                model.generateSpecificPassenger(from, to);
            }else{
                Logger.getLogger(Controller.class.getName()).warning("Invalid passenger data, ignoring  "+simulatorEvent.getFrom() +" -> "+ simulatorEvent.getTo());
            }
        }
    }

    private final class PassengerGenerationIntervalStrategy extends MyStrategy {
        @Override
        void execute(SimulatorEvent simulatorEvent) {
            int lowerBound = simulatorEvent.getMin();
            int upperBound = simulatorEvent.getMax();
            model.setNewPassengerCountersBound(lowerBound, upperBound);
        }
    }

    private final class ContinousSimulationStrategy extends MyStrategy {
        @Override
        void execute(SimulatorEvent simulatorEvent) {
            timer.setRepeats(simulatorEvent.isContinuous());
            timer.restart();
        }
    }

    private final class BusReleasingFrequencyStrategy extends MyStrategy {
        @Override
        void execute(SimulatorEvent simulatorEvent) {
            BusReleasingFrequency event = (BusReleasingFrequency) simulatorEvent;
            if (event.getFrequency() == 0)
            {
                //Zatrzymaj wypuszczanie autobusów
                model.busReleaseCounterState(false);
            }
            else
            {
                //Uruchamianie countera na wypadek jakby był wcześniej zatrzymany.
                model.busReleaseCounterState(true);
                model.setBusReleaseCounterValue(event.getFrequency());
            }
        }
    }

}
