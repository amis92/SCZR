package mockup;

import model.Passenger;

import java.io.Serializable;

/**
 * Created by ppeczek on 2014-05-28.
 */
public class MockupPassenger implements Serializable{
    private final int ID;
    private Long TIMESTAMP;
    private String destination;

    public MockupPassenger(Passenger passenger) {
        this.ID = passenger.getID();
        this.TIMESTAMP = passenger.getTIMESTAMP();
        this.destination = passenger.getDestination().getNAME();
    }

    public int getID() {
        return ID;
    }

    public Long getTIMESTAMP() {
        return TIMESTAMP;
    }

    public String getDestination() {
        return destination;
    }
}
