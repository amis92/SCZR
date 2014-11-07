package model;

import event.busevents.BusPutOutAll;
import event.busevents.BusPutOutPassengers;
import event.busevents.BusTookInPassengers;
import main.SimulatorConstants;
import model.counter.BreakAfterFinishedCounter;
import model.counter.LoopsCounter;
import model.counter.ReturnToDepotCooldown;
import model.counter.ToNextStopDistanceCounter;
import view.SimulatorEvent;

import java.io.Serializable;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <b>Bus</b>.<br>
 * <br>
 * Początkowo wszystkie stacjonują w <b>Zajezdni</b>.<br>
 * W razie wywołania przez <b>Moduł Zarządzający Komunikacją Miejską</b>, opuszczają <b>Zajezdnię</b> i jadą na <b>Pętlę</b>.<br>
 * Z <b>Pętli</b> wyrusza na <b>Linię</b>, odwiedza <b>M Przystanków</b> i wraca na <b>Pętlę</b>.<br>
 * Z <b>Pętli</b> według polecenia <b>Modułu Zarządzającego Komunikacją Miejską</b>, może zjechać do <b>Zajezdni</b>.<br>
 * Mieści <b>P Pasażerów</b>.<br>

 * Pomija <b>Przystanek</b>, jeśli żaden <b>Pasażer</b> nie chce wsiąść i wysiąść lub <b>Autobus</b> jest pełny i żaden <b>Pasażer</b> nie che wysiaść.<br>
 *
 * @author dan.krasniak
 */
public final class Bus implements EventListener, Serializable
{
    /**
     * How many <b>Passengers</b> can one <b>Bus</b> hold.
     */
    private static final int MAX_SEATS = SimulatorConstants.noOfSeatsInBus;
    /**
     * A container of currently held <b>Passengers</b>
     */
    public BlockingQueue<SimulatorEvent> blockingQueue;
    private SuperHashMap passengerList = new SuperHashMap();
    private BusStop currentBusStop;
    private BusState state;
    private ToNextStopDistanceCounter toNextStop;
    private LoopsCounter loopsToFinish;
    private BreakAfterFinishedCounter cooldownAfterLoops;
    private ReturnToDepotCooldown returnToDepotCooldown;
    private final int ID;
    private Map<BusState, BusBehaviorStrategy> busBehaviorStrategyMap = new HashMap<>();
    private boolean finishedLoops;


    private static class IDGenerator{
        private static int lastId = 0;

        public static int getNextId(){
            return lastId++;
        }
    }

    public Bus(BusStop startStation, LinkedBlockingQueue<SimulatorEvent> blockingQueue) {
        this.blockingQueue = blockingQueue;
        this.currentBusStop = startStation;
        this.state = BusState.READY_TO_GO;
        this.toNextStop = new ToNextStopDistanceCounter(blockingQueue, this.currentBusStop.getDistanceToNextStop(), this);
        this.loopsToFinish = new LoopsCounter(blockingQueue, SimulatorConstants.loops, this);
        this.cooldownAfterLoops = new BreakAfterFinishedCounter(blockingQueue, SimulatorConstants.cooldownAfterLoops, this);
        this.returnToDepotCooldown = new ReturnToDepotCooldown(blockingQueue, SimulatorConstants.depotTerminusDistance, this);
        this.busBehaviorStrategyMap = getBusBehaviorStrategyMap();
        ID = IDGenerator.getNextId();
    }

    private Map<BusState, BusBehaviorStrategy> getBusBehaviorStrategyMap() {
        final Map<BusState, BusBehaviorStrategy> resultMap = new HashMap<>();
        resultMap.put(BusState.READY_TO_GO, new BusIdleStrategy());
        resultMap.put(BusState.RUNNING, new BusOnRunStrategy());
        resultMap.put(BusState.WAITING, new BusWaitingForBusStopStrategy());
        resultMap.put(BusState.FINISHED, new BusReturningStrategy());
        resultMap.put(BusState.HAVING_BREAK, new BusReturnedStrategy());
        resultMap.put(BusState.PUT_OUT, new BusPutsOutPassengersStrategy());
        resultMap.put(BusState.TAKE_IN, new BusTakesInPassengersStrategy());
        resultMap.put(BusState.PUT_OUT_ALL, new BusPutsOutAllStrategy());
        return Collections.unmodifiableMap(resultMap);
    }

    public int getID(){
        return ID;
    }

    /**
     * @return the passengerList
     */
    public final SuperHashMap getPassengerList() {
        return passengerList;
    }

    public final void takeInPassengers() {
        if (!isFull() && !getCurrentBusStop().isEmpty()) {
            takePassenger(getCurrentBusStop());
        }
//        System.out.println("zajetosc busu: " + getPassengerList().size() + " zajetosc przystanku: " + getCurrentBusStop().getPassengerQueue().size());
        if (isFull() || getCurrentBusStop().isEmpty()) {
//            setState(BusState.RUNNING);
//            freeCurrentBusStop();
            try
            {
                blockingQueue.put(new BusTookInPassengers(this));
            } catch (final InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private final void takePassenger(BusStop busStop) {
//        System.out.println("kolejka: " + busStop.getPassengerQueue().size());
        Passenger passenger = busStop.takePassenger();
//        System.out.println("pasażer: " + passenger.getID());
//        System.out.println("kolejka: " + busStop.getPassengerQueue().size());
        if (passenger != null) {
            passengerList.put(passenger.getDestination(), passenger);
        }

    }

    private final void putOutPassengers() {
        putOutPassenger(getCurrentBusStop());
        if (!isGetOffRequestNow()) {
//            if (isGetOnRequestNow()) {
//                setState(BusState.TAKE_IN);
//            }
//            else {
//                freeCurrentBusStop();
//                setState(BusState.RUNNING);
//            }
            try
            {
                blockingQueue.put(new BusPutOutPassengers(this));
            } catch (final InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private final Passenger putOutPassenger(BusStop busStop) {
//        System.out.println("Liczba pasażerów:" + getPassengerList().size());
        Passenger passenger = getPassengerList().poll(busStop);
        System.out.println(busStop.getNAME() + ": " + getPassengerList().size());
//        System.out.println("Wysiadający pasażer:" + passenger.getID());
//        System.out.println("Liczba pasażerów:" + getPassengerList().size());
        return passenger;
    }

    private final void putOutAll() {
//        if (!isEmpty()) {
//        System.out.println("Liczba pasażerów:" + getPassengerList().size());
        transferPassenger(getCurrentBusStop());
//        if (isEmpty()) {
//            setState(BusState.FINISHED);
//            comeback();
//            freeCurrentBusStop();
//        }
//        }
//        System.out.println("Liczba pasażerów:" + getPassengerList().size());
        if (isEmpty()) {
            try {
                blockingQueue.put(new BusPutOutAll(this));
            } catch (final InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private final void transferPassenger(BusStop busStop) {
        BusStop current = getPassengerList().keySet().iterator().next();
        Passenger passenger = getPassengerList().poll(current);
        passenger.setTIMESTAMP(System.currentTimeMillis());
        busStop.getPassengerQueue().add(passenger);
    }

    public final void comeback() {
        setCounterToNextStop(SimulatorConstants.depotTerminusDistance);
    }
    /**
     * <b>isFull</b><br>
     *
     * @return true if the <b>Bus</b> is full.
     */
    public final boolean isFull() {
        return (getPassengerList().size() == MAX_SEATS);
    }

    public final boolean isEmpty() { return getPassengerList().isEmpty(); }

    /**
     * <b>getNumberOfFreeSeats</b><br>
     *
     * @return the number of free seats in the <b>Bus</b>
     */
    public final int getNumberOfFreeSeats() {
        return MAX_SEATS - getPassengerList().size();
    }

    /**
     * <b>getCurrentBusStop</b><br>
     * <br>
     * public BusStop <b>getCurrentBusStop</b>()
     *
     * @return current <b>BusStop</b>
     */
    public BusStop getCurrentBusStop() {
        return currentBusStop;
    }

    public void setCurrentBusStop(BusStop currentBusStop) {
        this.currentBusStop = currentBusStop;
    }

    public BusState getState() {
        return state;
    }

    public void setState(BusState state) {
//        System.out.println("nowy stan: " + state);
        this.state = state;
    }

    public ToNextStopDistanceCounter getToNextStop() {
        return toNextStop;
    }

    public LoopsCounter getLoopsToFinish() {
        return loopsToFinish;
    }

    /**
     * <b>getNextBusStop</b><br>
     * <br>
     * public BusStop <b>getNextBusStop</b>()
     *
     * @return next <b>BusStop</b>
     * @throws IndexOutOfBoundsException
     */
    public final BusStop getNextBusStop() {
        return currentBusStop.getNextBusStop();
    }

    /**
     * <b>move</b><br>
     */
    public final void move() {
        final BusBehaviorStrategy behaviorStrategy = busBehaviorStrategyMap.get(getState());
        behaviorStrategy.execute();
    }


    public void setCounterToNextStop(int length) {
        getToNextStop().initiateCounter(length);
    }

    public void reachStop(BusStop busStop)
    {
        System.out.println(this + " dojechał do " + busStop + " na " + this.getState() + " " +this.isFinishedLoops());
        setCurrentBusStop(busStop);
        setCounterToNextStop(getCurrentBusStop().getDistanceToNextStop());
    }

    public void reachNextStop() {
        reachStop(getNextBusStop());
    }

    public void reachDepot() {
        reachStop(BusDepot.getInstance());
    }
    
    public boolean isGetOffRequest(BusStop busStop) {
        return getPassengerList().containsKey(busStop) && !getPassengerList().get(busStop).isEmpty();
    }

    public boolean isGetOnRequest(BusStop busStop) {
        return !isFull() && !busStop.getPassengerQueue().isEmpty();
    }

    public boolean isGetOffRequestNow() {
        return isGetOffRequest(getCurrentBusStop());
    }

    public boolean isGetOnRequestNow() {
        return isGetOnRequest(getCurrentBusStop());
    }

    private void occupyBusStop(BusStop busStop) { busStop.setOccupied(this); }

    private void freeBusStop(BusStop busStop) { busStop.setOccupied(null); }

    public void occupyCurrentBusStop() {
        /***
         * Dojeżdża na przystanek.
         */
        reachNextStop();
//        if (this.getCurrentBusStop() instanceof BusTerminus) {
        System.out.println(this + " zajmuje " + this.getCurrentBusStop() + " stan: " + this.getState());
//        }
        /***
         * Semafor (zajmuję przystanek).
         */
        occupyBusStop(getCurrentBusStop());
    }

    public void freeCurrentBusStop() {
//        if (this.getCurrentBusStop() instanceof BusTerminus) {
        System.out.println(this + " zwalnia " + this.getCurrentBusStop() + " stan: " + this.getState() + " zajęty: " + this.getPassengerList().size());
//        }
        freeBusStop(getCurrentBusStop());
    }

    public boolean isNextStopOccupied() { return getNextBusStop().getOccupied()!=null; }

    private boolean onTerminusAlready = false;// ugly fix

    /***
     *
     * @return
     */
    public void terminusCheck() {
        if (getCurrentBusStop().getNextBusStop() instanceof BusTerminus) {
            if(!onTerminusAlready) {
                getLoopsToFinish().countdown();
                System.out.println(this + getCurrentBusStop().getNextBusStop().getNAME() + ": To finish:" + getLoopsToFinish().getValue());
                onTerminusAlready = true;
            }
        }else{
            onTerminusAlready = false;
        }
    }

    public boolean isFinishedLoops() {
        return finishedLoops;
    }

    public void setFinishedLoops(boolean finishedLoops) {
        this.finishedLoops = finishedLoops;
    }

    abstract private class BusBehaviorStrategy implements Serializable {
        abstract void execute();
    }

    /**
     * <b>Strategia autobusu czekającego na zajezdni na sygnał.</b>
     * Autobus nic nie robi.
     */
    private final class BusIdleStrategy extends BusBehaviorStrategy {
        @Override
        void execute() {
            //Do nothing
        }
    }

    /**
     * <b>Strategia jadącego autobusu.</b>
     * Dopóki autobus nie dojedzie do przystanka, to jest w drodze.
     */
    private final class BusOnRunStrategy extends BusBehaviorStrategy {
        @Override
        void execute() {
            toNextStop.countdown();
        }
    }

    /**
     * <b>Strategia autobusu czekającego na zwolnienie się przystanka.</b>
     * Jeśli inny autobus zajmował przystanek,
     * to ten sprawdza, czy przystanek jest nadal zajęty.
     * Jeśli jest wolny, to wjeżdża na niego i w przypadku zakończenia trasy
     * wysadza wszystkich pasażerów, w przeciwnym razie standardowo: najpierw opuszczają
     * go pasażerowie, dla których jest to przystanek docelowy, a następnie wsiadają do niego
     * kolejne osoby w miarę wolnych miejsc.
     *
     */
    private final class BusWaitingForBusStopStrategy extends BusBehaviorStrategy {
        @Override
        void execute() {
//            System.out.println(Bus.this + " w stanie: " + Bus.this.getState() + " na: " + Bus.this.getNextBusStop() + " zajęty przez: " + Bus.this.getNextBusStop().getOccupied());
//            if (Bus.this.getNextBusStop().getOccupied() != null) {
//                Bus chuj = Bus.this.getNextBusStop().getOccupied();
//                System.out.println("przez: " + chuj + " w stanie: " + chuj.getState() + " w pełni: " + chuj.getPassengerList().size() + " na przystanku: " + chuj.getCurrentBusStop() + " aa dupa: " + chuj.getCurrentBusStop().getPassengerQueue().size());
//            }
            if (!Bus.this.isNextStopOccupied()) {
                reachNextStop();
                occupyCurrentBusStop();
                if (isFinishedLoops()) {
                    setState(BusState.PUT_OUT_ALL);
                }
                else {
                    if (isGetOffRequestNow()) {
                        setState(BusState.PUT_OUT);
                    }
                    else {
                        setState(BusState.TAKE_IN);
                    }
                }
            }
        }
    }

    /**
     * <b>Strategia autobusu wracającego do zajezdni.</b>
     * Autobus wraca do zajezdni.
     */
    private final class BusReturningStrategy extends BusBehaviorStrategy {
        @Override
        void execute() {
            returnToDepotCooldown.countdown();
            System.out.println(this.getClass());
        }
    }

    /**
     * <b>Strategia autobusu odbywającego przerwę po kursie.</b>
     * Autobus odczekuje i staje się gotowy do dalszej drogi.
     */
    private final class BusReturnedStrategy extends BusBehaviorStrategy {
        @Override
        void execute() {
            cooldownAfterLoops.countdown();
        }
    }

    /**
     * Strategia wypuszczania pasażerów.
     */
    private final class BusPutsOutPassengersStrategy extends BusBehaviorStrategy {
        @Override
        void execute() {
            Bus.this.putOutPassengers();
        }
    }

    /**
     * Strategia zabierania pasażerów.
     */
    private final class BusTakesInPassengersStrategy extends BusBehaviorStrategy {
        @Override
        void execute() {
            Bus.this.takeInPassengers();
        }
    }

    /**
     * Strategia wypuszczania wszystkich pasażerów
     */
    private final class BusPutsOutAllStrategy extends BusBehaviorStrategy {
        @Override
        void execute() {
            Bus.this.putOutAll();
        }
    }
}
