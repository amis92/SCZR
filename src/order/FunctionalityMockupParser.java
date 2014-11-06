package order;

import mockup.Mockup;

/**
 * Zbiór funkconalności oczekiwanych od obiektów mających
 * 'rozumieć' makiety Pana Daniela
 *
 * @author Maciej Majewski
 */
public interface FunctionalityMockupParser {

    /**
     * Pozostaje przetworzyć otrzymaną makietę.
     *
     * @param fresh makieta którą dostajesz
     */
    public void newMockup(Mockup fresh);
}
