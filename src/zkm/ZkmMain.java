package zkm;

import event.BusReleasingFrequency;
import main.SimulatorConstants;
import mockup.*;
import model.*;
import network.Client;
import view.SimulatorEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Klasa zarządzająca wypuszczaniem autobusów z pętli Symulatora aby zoptymalizować ich rozkłąd jazdy.
 * @author Rafal Jagielski
 */
public class ZkmMain extends Thread {

    private String host = ZkmConstants.host;
    private int port = ZkmConstants.port;
    private Client<Mockup> sc = new Client<Mockup>(host, port);
    /** zmienna przechowująca ilość kroków potrzebną do jednokrotnego przejazdu trasy
     * (nie zlicza czasu potrzebnego na wsiadanie i wysiadanie pasażerów)
     */
    private Integer loopTimeMinute = 0;

    /**
     * Konstruktor jednocześnie inicjalizuje wartość paramteru loopTimeMinute korzystając z tablicy
     * na stałe zdefiniowanej w klasie main.SimulatorConstants.
     */
    public ZkmMain() {
        for (Pair<String, Integer> pair : SimulatorConstants.busStopSettings){
            loopTimeMinute += pair.getValue();
        }
    }

    public static void main(String[] args)
    {
        final ZkmMain zkmMain = new ZkmMain();

        //Wątek nasłuchujący sygnału z klawiatury. Gdy taki otrzyma kończy pracę programu.
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                boolean cont = true;
                while(cont)
                {
                    try
                    {
                        Thread.sleep(500);
                        if (System.in.available() != 0) {
                            zkmMain.interruptMe();
                            cont = false;
                        }
                    } catch (Exception e)
                    {
                        // Nic nie trzeba robić.
                        // Jednynym efektem będzie ponowna próba sprawdzenia System.in
                        e.printStackTrace();
                    }
                }
                System.out.println("ZkmMain is interrupted.");
            }
        }).start();

        zkmMain.start();
    }

    /**
     * Główna metoda programu zawierająca pętlę, która przetwarza kolejne makiety.
     * Oblicza z nich zgeneralizowane informacje, na podstawie których jest podejmowana decyzja.
     */
    @Override // skoro dziedziczy sie po Thread to może urwa by skorzystać
    public void run() {
        sc.connect();

        System.out.println("Press enter key to stop.");

        Mockup mockup;
        while(true) {// wyjdzie na twardo jak sie klawiaturnie
            //Odbieranie makiety
            mockup = receiveMockup();
            List<MockupBus> buses = mockup.getBuses();
            List<MockupBusStop> busStops = mockup.getBusStops();

            //Wyliczanie współczynników
            Integer noOfBuses = 0;
            Integer freeSeatsNr = 0;
            Integer generalPeopleWaitingNr = 0;
            Long generalSumOfWaitingTime = 0L;

            for (MockupBus bus : buses) {
                if (!bus.getCurrentBusStop().equals(SimulatorConstants.depotName))
                //tylko autobusy nie będące w zajezdni
                {
                    noOfBuses += 1;
                    freeSeatsNr += SimulatorConstants.noOfSeatsInBus - bus.getPassengerList().size();
                    System.out.println("Pasażerów w autobusie: " + bus.getPassengerList().size());
                }
            }

            for (MockupBusStop busStop : busStops) {
                generalPeopleWaitingNr += busStop.getPassengerQueue().size();

                for(MockupPassenger passenger : busStop.getPassengerQueue())
                {
                    System.out.println("Mockup time: " + mockup.getCurrentTime() + "; Passenger timestamp: " + passenger.getTIMESTAMP());
                    generalSumOfWaitingTime += mockup.getCurrentTime() - passenger.getTIMESTAMP();
                }
            }

            //delegacja podjęcia decyzji do innej metody
            makeDecision(noOfBuses, freeSeatsNr, generalPeopleWaitingNr,
                    generalSumOfWaitingTime);

        }
    }

    /**
     * Metoda odbierająca makietę z wątku klienta (network.Client) serwera. Jeśli w międzyczasie odebrane
     * zostało kilka makiet, to przekazywana jest tylko ta wysłana najpóźniej. Reszta jest ignorowana
     * (taka sytuacja może nastąpić, gdy zostanie utracone połączenie).
     * @return Zwracanym obiektem jest makieta nadana na serwerze.
     */
    private Mockup receiveMockup() {
        Mockup mockup = null;
        LinkedBlockingQueue<Mockup> queueOfOrders = sc.getEventsBlockingQueue();
            try{
                do{
                    mockup = queueOfOrders.take();
                }while (!queueOfOrders.isEmpty()); // przewijanie do ostatniej odebranej makiety
            }
            catch (InterruptedException e){//wyjątek tylko jak Thread.interrupt()
                sc.closeConnection();
                System.out.println("ZKM has stopped.");
                System.exit(0);
            }

        return mockup;
    }

    /**
     * Metoda podejmująca decyzję i odsyłająca odpowiednią informację do Symulatora.
     * @param noOfBuses liczba autobusów jeżdżących (tzn. tych, które nie znajdują się aktualnie w pętli)
     * @param freeSeatsNr liczba wolnych miejsc w autobusach jeżdżących
     * @param generalPeopleWaitingNr liczba ludzi czekająca na przystankach
     * @param generalSumOfWaitingTime suma czasów spędzonych przez w/w ludzi na przystakach
     */
    private void makeDecision(Integer noOfBuses, Integer freeSeatsNr, Integer generalPeopleWaitingNr,
                              Long generalSumOfWaitingTime)
    {
        // W tym miejscu obliczana jest potrzebna liczba autobusów do zaspokojenia potrzeb Swiata
        int peopleInsideBuses = noOfBuses*SimulatorConstants.noOfSeatsInBus - freeSeatsNr;
        int peopleInTheWorld = peopleInsideBuses + generalPeopleWaitingNr;
        int howManyBuses = (int) Math.ceil(peopleInTheWorld / (double) SimulatorConstants.noOfSeatsInBus) ;

        System.out.println("HowManyBuses: " + howManyBuses + "; CurrentNumberOfBuses: " + noOfBuses +  "; freeSeatsNr: " + freeSeatsNr
                + "; generalPeopleWaitingNr: " + generalPeopleWaitingNr + "; generalSumOfWaitingTime: " + generalSumOfWaitingTime);

        if (howManyBuses == 0)
        {
            //Jeśli nie potrzeba żadnego autobusu, to zajezdnia ma zaprzestać wysyłanie autobusów
            sc.send(new BusReleasingFrequency(0));
        }
        else
        {
            // Obliczana jest częstotliwość wypuszczania autobusó, tak aby znajdowało się w obiegu dokładnie
            // tyle autobusów ile zostało wyliczonych
            int newFrequency = (loopTimeMinute * SimulatorConstants.loops) / (howManyBuses);
            sc.send(new BusReleasingFrequency(newFrequency));
        }

        /**
         * TODO: Potencjalne problemy:
         *  - gdy nagle zmieni się sytuacja na wymagającą większej ilości autobusów, wtedy Counter i tak będzie musiał
         *  zliczyć do zera zanim zmieni się jego wartość. Może to spowodować opóźnioną reakcję na zapotrzebowanie.
         *  - nie jest brany pod uwagę czas potrzebny na wysiadanie i wsiadanie. Jeśli będzie to proporcjojnalnie dużo
         *  w stosunku do czasu objazdu trasy, to trzeba sprawdzić efekty :-)
         */
    }

    /**
     * Przerywa oczekiwanie na cokolwiek dochodzącego do kolejki i spierdala ze wszechświata.
     */
    private void interruptMe(){
        this.interrupt();
    }
}
