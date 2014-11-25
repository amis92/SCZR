package burtis.common.events;

/**
 *
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 */
public class PassengerQueryEvent extends SimulationEvent {

    public PassengerQueryEvent(String sender, String[] recipients) {
        super(sender, recipients);
    }
    
}
