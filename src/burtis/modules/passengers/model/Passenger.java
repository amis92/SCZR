package burtis.modules.passengers.model;

/**
 * Representation of single passenger. 
 * 
 * @author Mikołaj Sowiński
 */
public class Passenger 
{  
    
    /**
     * Passenger id.
     */
    private final int id;
    
    /**
     * Passenger origin.
     * 
     * Bus stop where passenger was generated.
     */
    private final BusStop origin;
    
    /**
     * Passenger destination.
     */
    private final BusStop destination;
    
    /**
     * Time from generation to embark or time from generation.
     */
    private long waitingTime;
    
    /**
     * Bus assigned to the passenger.
     */
    private Bus bus = null;
    
    /**
     * Generates passenger ids.
     */
    private static class IDGenerator
    {
        /**
         * Recent bus id.
         */
        private static int lastId = 0;

        /**
         * Returns id of the next bus.
         * @return new id
         */
        public static int getNextId()
        {
            return lastId++;
        }
    }
    
    /**
     * Constructor.
     * 
     * @param origin origin bus stop
     * @param destination destination bus stop
     */
    public Passenger(BusStop origin, BusStop destination) {
        this.id = IDGenerator.getNextId();
        this.origin = origin;
        this.destination = destination;
    }

/* ##############################################
 * GETTERS AND SETTERS
 * ########################################### */
    
    public int getId() {
        return id;
    }

    public BusStop getOrigin() {
        return origin;
    }

    public BusStop getDestination() {
        return destination;
    }

    public long getWaitingTime() {
        return waitingTime;
    }
    
    public void setBus(Bus bus)
    {
        this.bus = bus;
    }

/* ##############################################
 * END OF GETTERS AND SETTERS
 * ########################################### */
    
    /**
     * Increases waiting time.
     * 
     * Increases waiting time if bus is null.
     */
    public void tickPassenger() {
        if(bus == null) {
            waitingTime++;
        }
    }
    
    @Override
    public String toString() {
        return "Passenger{" + "id=" + id + ", origin=" + origin.getName() + ", destination=" + destination.getName() + ", waitingTime=" + waitingTime + ", bus=" + bus.getId() + '}';
    }    
        
}
