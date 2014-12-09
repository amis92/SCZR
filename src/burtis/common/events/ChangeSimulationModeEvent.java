package burtis.common.events;

public class ChangeSimulationModeEvent extends SimulationEvent
{
    private static final long serialVersionUID = -976705205260973429L;

    public enum State {
        STOPPED, RUNNING, STEPMODE, NEXTEVENT
    }
    
    private final State targetState;
    
    public ChangeSimulationModeEvent(String sender, State targetState) {
        super(sender);
        this.targetState = targetState; 
    }
    
    public State getTargetState() {
        return targetState;
    }
}
