/**
 * 
 */
package burtis.common.events;

/**
 * @author Mikołaj Sowiński
 *
 */
public class TickEvent extends SimulationEvent
{
    private final long iteration;
    
    public TickEvent(String sender, long iteration) {
        super(sender);
        this.iteration = iteration;
    }
    
    public long iteration() {
        return iteration;
    }
    
    private static final long serialVersionUID = -7732041429086152989L;
  
}
