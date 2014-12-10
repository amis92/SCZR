package burtis.modules.gui.controller;

import burtis.modules.gui.events.ProgramEvent;

/**
 * interfejs definiujacy odpowiedz kontorlera na zapytanie plynace z modelu 
 * aby z niego skorzystac nalezy zdefiniowac funkcje go
 * @version 1.0
 */
public interface ProgramAction {
	abstract public void go(ProgramEvent e);
}