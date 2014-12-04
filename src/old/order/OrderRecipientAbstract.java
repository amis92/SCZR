package order;

import java.util.logging.Logger;

/**
 * Jak komuś się nie chce tego pisać, to może odziedziczyć.
 *
 * @param <T> interfejs funkcjonalny.
 *            TODO Przetestować.
 * @author Maciej Majewski
 */
public abstract class OrderRecipientAbstract<T> implements OrderRecipient<T> {
    @Override
    public void executeOrder(Order<T> toExec) {
        Logger.getLogger(OrderRecipient.class.getName(), "Executing order of type: " + toExec.getClass().getName());
        toExec.execute((T) this); // W sumie nie testowałem czy ta linijka zadziała.
        //W sumie powinna, chyba że interf. funkcjonalny T nie będzie implementowany
        //przez dziedzica.
    }
}
