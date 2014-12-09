/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events;

/**
 *
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 */
public class PassengersOnBusStopEvent extends SimulationEvent {

    private int passengersNo;

    /**
     * Get the value of passengersNo
     *
     * @return the value of passengersNo
     */
    public int getPassengersNo() {
        return passengersNo;
    }

    public PassengersOnBusStopEvent(String sender, String[] recipients, int passengersNo) {
        super(sender, recipients);
        this.passengersNo = passengersNo;
    }
    
}
