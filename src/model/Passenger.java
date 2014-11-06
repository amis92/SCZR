package model;


import java.io.Serializable;
import java.util.UUID;

/**
 * <b>Passenger</b><br>
 * Podróżny, pojawia się losowo na <b>Przystankach</b> z losowym <b>Przystankiem</b> docelowym.<br>
 * Na <b>Przystanku</b> tworzy kolejkę FIFO.
 * Moment pojawienia się <b>Pasażera</b> na przystanku oznaczany jako <b>TimeStamp</b>.
 */
public class Passenger implements Serializable
{
    /**
     * Unique id
     */
    private final int ID;
    /**
     * On which step the <b>Passenger</b> appeared at the <b>BusStop</b>
     */
    private Long TIMESTAMP;
    /**
     * <b>Passengers'</b> designated <b>BusStop</b>.
     */

    private static class IDGenerator{
        private static int lastId = 0;

        public static int getNextId(){
            return lastId++;
        }
    }
    private BusStop destination;

    /**
     * @param destination Wymagane.
     */
    public Passenger(final BusStop destination) {
        this.destination = destination;
        this.TIMESTAMP = SimulationTimer.getInstance().getTime();
        this.ID = IDGenerator.getNextId();
        //System.out.println("TIMESTAMP: " + this.TIMESTAMP + "; Passenger: " + this.ID);
    }

    /**
     * @return the ID
     */
    public final int getID() {
        return ID;
    }

    /**
     * @return the TIMESTAMP
     */
    public final Long getTIMESTAMP() {
        return TIMESTAMP;
    }

    public void setTIMESTAMP(Long TIMESTAMP) {
        this.TIMESTAMP = TIMESTAMP;
    }

    /**
     * @return the destination
     */
    public final BusStop getDestination() {
        return destination;
    }
}
