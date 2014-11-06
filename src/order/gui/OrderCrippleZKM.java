package order.gui;

import order.FunctionalityServer;
import order.ServerOrderAbstract;

/**
 * Polecenie wydawane przez view
 *
 * @author Maciej Majewski
 */
public class OrderCrippleZKM extends ServerOrderAbstract<FunctionalityServer> {

    private final boolean cripple;

    /**
     * @param cripple true jeśli przesyłanie rozkazów od zkm ma być ignorowane
     */
    public OrderCrippleZKM(boolean cripple) {
        this.cripple = cripple;
    }


    @Override
    public void execute(FunctionalityServer subject) {
        subject.crippleZKM(cripple);
    }

}
