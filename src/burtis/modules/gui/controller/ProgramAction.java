package burtis.modules.gui.controller;

import burtis.modules.gui.events.ProgramEvent;

/**
 * Interfejs definiujacy odpowiedź kontrolera na zapytanie płynące z modelu.
 * 
 * @version 1.0
 */
@FunctionalInterface
public interface ProgramAction
{
    public void go(ProgramEvent e);
}
