package order;

/**
 * Szablon interfejsu dla rozkazów.
 * który zawiera funkcjonalność do udostępnienia.
 * Implementacja powinna być serializowalna.
 * Małe przypomnienie: w Javie szablony działają inaczej niż w C++
 * Rozszerza Comparable po to, by Pan Daniel mógł sobie szeregować rozkazy
 * (wystarczy je będzie wrzucić w PriorityQueue)
 *
 * @param <T> powinien być konkretyzowany interfejsem opisującym
 *            udostępnianą przez przyjmującego_rozkaz funkcjonalność.
 * @author Maciej Majewski
 */
public interface Order<T> {

    /**
     * Wykonuje dany rozkaz. Implementacja to zwykle
     * `subject.functionality()`
     *
     * @param subject
     */
    public void execute(T subject);

}
