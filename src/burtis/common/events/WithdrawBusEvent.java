package burtis.common.events;

/**
 *
 * @author Mikołaj Sowiński
 */
public class WithdrawBusEvent extends SimulationEvent {

    private int busId;

    /**
     * Get the value of busId
     *
     * @return the value of busId
     */
    public int busId() {
        return busId;
    }

    
    public WithdrawBusEvent(String sender, String[] recipients, int busId) {
        super(sender, recipients);
        this.busId = busId;
    }
    
}
