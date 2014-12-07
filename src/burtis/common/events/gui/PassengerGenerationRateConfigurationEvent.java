/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events.gui;

import burtis.common.events.ConfigurationEvent;

/**
 *
 * @author Mikołaj Sowiński
 */
public class PassengerGenerationRateConfigurationEvent extends ConfigurationEvent {
    
    private final int generationCycleLength;
    private final int passengersPerCycle;

    public PassengerGenerationRateConfigurationEvent(int generationCycleLength, int passengersPerCycle, String sender) {
        super(sender);
        this.generationCycleLength = generationCycleLength;
        this.passengersPerCycle = passengersPerCycle;
    }

    public int getGenerationCycleLength() {
        return generationCycleLength;
    }

    public int getPassengersPerCycle() {
        return passengersPerCycle;
    }
    
    
    
    
}
