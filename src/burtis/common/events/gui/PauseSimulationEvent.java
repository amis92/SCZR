/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.events.gui;

import burtis.common.events.SimulationEvent;

/**
 *
 * @author Mikołaj Sowiński <mikolaj.sowinski@gmail.com>
 */
public class PauseSimulationEvent extends SimulationEvent {

    public PauseSimulationEvent(String sender) {
        super(sender);
    }
    
}
