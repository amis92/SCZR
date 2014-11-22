package burtis.modules.sync;

import burtis.common.events.ChangeSimulationModeEvent.State;

public class StateThreadTerminationException extends Exception
{
    private State from;
    private State to;
    
    public StateThreadTerminationException(State from, State to) {
        this.from = from;
        this.to = to;
    }
    
    public State from() { return from; }
    public State to() { return to; }
}
