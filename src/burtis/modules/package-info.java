/**
 * BUsRealTImeSimulator - BURTIS
 * 
 * Simulates random passenger behaviours, while optimising bus schedule.
 * Written in client-server architecture, server provides only 
 * necessary means of communication between modules, of which each
 * operates independently.
 * 
 * SCZR 2014Z course project
 * 
 * Each module has it's responsibilities:
 * <ul>
 * <li><b>busscheduler</b> - optimization of bus traffic;</li>
 * <li><b>gui</b> - displays important information;</li>
 * <li><b>network</b> - enables communication across network,
 *  so that modules can work independently;</li>
 * <li><b>passengers</b> - entering and leaving buses;</li>
 * <li><b>simulation</b> - manages model of simulation;
 *  without it simulation doesn't work;</li>
 * <li><b>sync</b> - synchronizes modules to work in the same time frames;</li>
 * </ul>
 * 

 * @author Rafał Braun
 * @author Kamil Drożdżał
 * @author Amadeusz Sadowski
 * @author Mikołaj Sowiński
 * @author Piotr Suchorab
 *
 */
package burtis.modules;
