package burtis.modules.busscheduler;

import java.util.List;

import burtis.common.constants.SimulationModuleConsts;
import burtis.common.mockups.Mockup;
import burtis.common.mockups.MockupBus;
import burtis.common.mockups.MockupBusStop;
import burtis.common.mockups.MockupPassenger;

public class BusScheduler
{
    /**
     * zmienna przechowująca ilość kroków potrzebną do jednokrotnego przejazdu
     * trasy (nie zlicza czasu potrzebnego na wsiadanie i wysiadanie pasażerów)
     */
    private Integer loopTimeMinute = 0;

    public void receive(Mockup mockup)
    {
        List<MockupBus> busList = mockup.getBuses();
        List<MockupBusStop> busStops = mockup.getBusStops();
        int passengersSittingTotal = countPassengersSitting(busList);
        int waitingPassengersTotal = 0;
        Long waitingTimeTotal = 0L;
        for (MockupBusStop busStop : busStops)
        {
            waitingPassengersTotal += busStop.getPassengerCount();
            for (MockupPassenger passenger : busStop.getPassengers())
            {
                long waitingTime = 0; /* TODO waiting time */
                System.out.println("Mockup time: " + mockup.getCurrentTime()
                        + "; Passenger timestamp: " + waitingTime);
                waitingTimeTotal += mockup.getCurrentTime() - waitingTime;
            }
        }
        makeDecision(passengersSittingTotal, waitingPassengersTotal,
                waitingTimeTotal);
    }

    private int countPassengersSitting(List<MockupBus> busList)
    {
        int passengersSittingTotal = 0;
        for (MockupBus bus : busList)
        {
            int passengerCount = bus.getPassengerList().size();
            if (false /* TODO sprawdź czy jest z zajezdni */)
            {
                passengersSittingTotal += passengerCount;
                // System.out.println("Pasażerów w autobusie: " +
                // passengerCount);
            }
        }
        return passengersSittingTotal;
    }

    private void makeDecision(Integer passengersSittingTotal,
            Integer waitingPassengersTotal, Long waitingTimeTotal)
    {
        int peopleInTheWorld = passengersSittingTotal + waitingPassengersTotal;
        int howManyBuses = (int) Math.ceil(peopleInTheWorld
                / (double) SimulationModuleConsts.BUS_CAPACITY);
        // System.out.println("HowManyBuses: " + howManyBuses
        // + "; CurrentNumberOfBuses: " + noOfBuses + "; freeSeatsNr: "
        // + freeSeatsNr + "; generalPeopleWaitingNr: "
        // + generalPeopleWaitingNr + "; generalSumOfWaitingTime: "
        // + generalSumOfWaitingTime);
        if (howManyBuses == 0)
        {
            setNewFrequency(0);
        }
        else
        {
            int newFrequency = (loopTimeMinute * SimulationModuleConsts.BUS_MAX_CYCLES)
                    / (howManyBuses);
            setNewFrequency(newFrequency);
        }
        /**
         * TODO: Potencjalne problemy: - gdy nagle zmieni się sytuacja na
         * wymagającą większej ilości autobusów, wtedy Counter i tak będzie
         * musiał zliczyć do zera zanim zmieni się jego wartość. Może to
         * spowodować opóźnioną reakcję na zapotrzebowanie. - nie jest brany pod
         * uwagę czas potrzebny na wysiadanie i wsiadanie. Jeśli będzie to
         * proporcjojnalnie dużo w stosunku do czasu objazdu trasy, to trzeba
         * sprawdzić efekty :-)
         */
    }

    private void setNewFrequency(int newFrequency)
    {
    }
}
