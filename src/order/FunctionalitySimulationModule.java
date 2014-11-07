package order;

/**
 * Funkcjonalność oczekiwana od modułu symulujączego rzeczywistość.
 *
 * @author Maciej Majewski
 */
public interface FunctionalitySimulationModule {

    /**
     * Polecenie zatrymania/wznowienia symulacji [view]
     *
     * @param patataj false jeśli symulacja ma być wstrzymana
     */
    public void runSimulation(boolean patataj);

    /**
     * Zmiana trybu symulacji [view]
     *
     * @param goSlower true, jeśli należy postępować krokowo
     */
    public void stepSimulation(boolean goSlower);

    /**
     * Ustalenie czasów generacji podróżnych [view]
     *
     * @param minGen najmniejsza liczba kroków przed ponowną generacją podróżnego
     * @param maxGen największa ilość kroków od ostatniej generacji podróżnego
     */
    public void passengerGenerationConfig(int minGen, int maxGen);

    /**
     * Polecenie umieszczenia nowego podróżnego w symulacji.
     *
     * @param busStopStart Nazwa przystanku startowego
     * @param busStopStop  Nazwa przystanku docelowego
     */
    public void newPassenger(String busStopStart, String busStopStop);


    //////////////////////////////////////////////////
    //////////      THERE ARE, WHO KNOWS    //////////
    //////////      UNFINISHED CODES        //////////
    //////////      WHICH LIE BELOW         //////////
    //////////      AND HATE YOU ALL        //////////
    //////////////////////////////////////////////////


    /**
     * Polecenie wypuszczenia nowego autobusu z zajezdni [zkm]
     */
    // TODO? możliwy brak identyfikatora
    public void releaseBus();

    /**
     * Polecenie zjazdu do zajezdni [zkm]
     */
    // TODO brak identyfikatora. Chyba że inaczej jest to realizowane,
    // tym licznikiem czy coś
    public void trapBus();

    /**
     * Polecenie zmiany rozkładu_jazdy/liczby_okrążeń [zkm]
     */
    // TODO tutaj wyrażnie brakuje parametrów (id czy coś)
    // Można zrezygnować z tej lub tej powyższej funkcji
    public void updateBus();
}
