package order;

/**
 * Wspólny interfejs dla odbiorców rozkazów.
 * W sumie formalizm.
 *
 * @author Maciej Majewski
 */
public interface OrderRecipient<T> {

    /**
     * Wykonanie rozkazu.
     *
     * @param toExec rozkaz do wykonania.
     * @see OrderRecipientAbstract
     */
    void executeOrder(Order<T> toExec);
}
