package order.sim;

import mockup.Mockup;
import order.FunctionalityMockupParser;
import order.Order;

/**
 * Polecenie przetworzenia makiety.
 *
 * @author Maciej Majewski
 */
public class OrderParseMockup implements Order<FunctionalityMockupParser> {
    private final Mockup makieta;

    public OrderParseMockup(Mockup makieta) {
        this.makieta = makieta;
    }

    @Override
    public void execute(FunctionalityMockupParser subject) {
        //subject.newMockup(makieta);
    }

}
