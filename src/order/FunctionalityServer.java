package order;

/**
 * Funkcjonalność serwera.
 * - COOOO?!?!11! - zapytasz
 * Zaiste, słuszne to zdziwko. W końcu serwer jedynie za woła roboczego
 * co to rozkazy nosi winien działać. Cóż to za herezje więc tutaj poczyniam?
 * - Potrzybny jest, waćpanie. Trza nam, jakbyś się waćpan w pergamin
 * projektowy wczytał, awarie symulować.
 * - Toż tego w rzeczonym pergaminie nie uświadczysz, panie waćpanie!
 * - Mieć rację możesz jedynie w swój się patrząc, niepiśmienny pomiocie tatarski.
 * Kiedy Prowadzący nam projek przed uszyma stawiał, prawił że takoż ma być.
 * 'Nie wygrasz z takim, co to myśli że wie wszystko.' ~sentencja na dziś pana M
 *
 * @author Maciej Majewski
 */
public interface FunctionalityServer {

    /**
     * Polecenie blokowania view. W sumie samobój, bo pewnie view właśnie je będzie wydawać :)
     * Chyba, że będzie zaimplementowane na timerze.
     *
     * @param cripple true jeśli rozkazy od view mają być ignorowane.
     */
    public void crippleGUI(boolean cripple);

    /**
     * Polecenie blokowania zkm.
     *
     * @param cripple true jeśli rozkazy od zkm mają być ignorowane.
     */
    public void crippleZKM(boolean cripple);

}
