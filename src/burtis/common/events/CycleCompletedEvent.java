package burtis.common.events;

/**
 * Event to be send upon module main loop completion.
 * 
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 */
public class CycleCompletedEvent extends SimulationEvent {
    
    /**
     * Which iteration it concerns.
     */
    private final long iteration;

    public CycleCompletedEvent(String sender, long iteration) {
        super(sender);
        this.iteration = iteration;
    }
   
    public long iteration() {
        return iteration;
    }
    
}
