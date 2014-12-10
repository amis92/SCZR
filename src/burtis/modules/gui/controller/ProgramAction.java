package burtis.modules.gui.controller;

import burtis.modules.gui.events.ProgramEvent;

/**
 * interfejs definiujacy odpowiedz kontorlera na zapytanie plynace z modelu 
 * aby z niego skorzystac nalezy zdefiniowac funkcje go
 * @version 1.0
 */
@FunctionalInterface
public interface ProgramAction {
	public void go(ProgramEvent e);
}