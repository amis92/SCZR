package burtis.modules.simulation.models;

public class Bus
{
    public enum State {
        DEPOT, BUSSTOP, RUNNING, TERMINUS
    }
   
    private final int id;
    private int position = 0;
       
    private State state;
    
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
    public Bus() {
        id = IDGenerator.getNextId();
        state = State.DEPOT;
    }
    
    public int getId() {
        return id;
    }
    
    public String getState() {
        return "BUS: " + id + ", POS: " + position + " S: " + state;
    }
}
