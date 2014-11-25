package burtis.common.events;

/**
 *
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 */
public class GetBusStopsEvent extends SimulationEvent {

    public GetBusStopsEvent(String sender, String[] recipients) {
        super(sender, recipients);
    }
    
}
