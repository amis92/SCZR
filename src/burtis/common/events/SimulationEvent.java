package burtis.common.events;

import java.io.Serializable;

public class SimulationEvent implements Serializable
{

    private static final long serialVersionUID = 1L;
    
    private final String sender;
    
    public SimulationEvent(String sender) {
        this.sender = sender;
    }
    
    public String sender() {
        return sender;
    }
    
}
