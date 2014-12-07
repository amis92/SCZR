package burtis.modules.simulation.models;

public class Bus
{
    public enum State {
        DEPOT, BUSSTOP, RUNNING, TERMINUS
    }
   
    private final int id;
    
    private final int capacity;
    
    private int numberOfPassengers;
    
    /**
     * Next bus stop defined by passengers.
     */
    private int nextBusStopId;
    
    /**
     * Next bus stop on the line.
     */
    private int closestBusStopId;
   
    private long startAt;
    private boolean goToDepot;
    
    private int position = 0;
    
    private State state;
    
    /**
     * Get the value of closestBusStop
     *
     * @return the value of closestBusStop
     */
    public int getClosestBusStopId() {
        return closestBusStopId;
    }
    
    public int getFreePlaces() {
        return capacity-numberOfPassengers;
    }

    /**
     * Set the value of closestBusStop
     *
     * @param closestBusStop new value of closestBusStop
     */
    public void setClosestBusStopId(int closestBusStopId) {
        this.closestBusStopId = closestBusStopId;
    }

    /**
     * Get the value of state
     *
     * @return the value of state
     */
    public State getState() {
        return state;
    }

    /**
     * Set the value of state
     *
     * @param state new value of state
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Get the value of position
     *
     * @return the value of position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Set the value of position
     *
     * @param position new value of position
     */
    public void setPosition(int position) {
        this.position = position;
    }
    
    /**
     * Get the value of nextBusStop
     *
     * @return the value of nextBusStop
     */
    public int getNextBusStopId() {
        return nextBusStopId;
    }

    /**
     * Set the value of nextBusStop
     *
     * @param nextBusStop new value of nextBusStop
     */
    public void setNextBusStop(int nextBusStop) {
        this.nextBusStopId = nextBusStopId;
    }

    /**
     * Get the value of goToDepot
     *
     * @return the value of goToDepot
     */
    public boolean isGoToDepot() {
        return goToDepot;
    }

    /**
     * Set the value of goToDepot
     *
     * @param goToDepot new value of goToDepot
     */
    public void setGoToDepot(boolean goToDepot) {
        this.goToDepot = goToDepot;
    }

    /**
     * Get the value of startAt
     *
     * @return the value of startAt
     */
    public long getStartAt() {
        return startAt;
    }

    /**
     * Set the value of startAt
     *
     * @param startAt new value of startAt
     */
    public void setStartAt(long startAt) {
        this.startAt = startAt;
    }
    
    private static class IDGenerator
    {
        private static int lastId = 0;
        
        public static int getNextId(){
            return lastId++;
        }   
    }
    
    /**
     * Constructor of Bus class.
     * Buses are always created in DEPOT state.
     */
    public Bus(int capacity) {
        id = IDGenerator.getNextId();
        state = State.DEPOT;
        this.capacity = capacity;
    }
    
    public int getId() {
        return id;
    }

    public String getStateString() {
        return "BUS: " + id + ", POS: " + position + " S: " + state;
    }
}
