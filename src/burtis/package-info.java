/**
 * BUs Real TIme Simulator - <b>BURTIS</b>
 * 
 * <p>
 * Simulates random passenger behaviours, while optimising bus schedule. Written
 * in client-server architecture, server provides only necessary means of
 * communication between modules, of which each operates independently.
 * </p>
 * 
 * <p>
 * Short summary of modules and their roles:
 * <ul>
 * <li><i>Network</i> - provides network communication backend; Server which is
 * central to communication and client abstact classes for easy implementation
 * by iheritance.</li>
 * <li><i>GUI</i> - allows end user to control and peek at simulation's state.</li>
 * <li><i>Simulation</i> - controls buses, their routes and sending them from
 * terminus.</li>
 * <li><i>Passengers</i> - manages passengers, their generation and position.</li>
 * <li><i>Bus Scheduler</i> - optimises amount of buses en route and may change
 * frequency of bus departure from terminus.</li>
 * <li><i>Synchronization</i> - the clock of simulation; dictates ticks which
 * trigger computation of next step status in every active module.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Events</b> are tokens with which modules send messages inbetween. Server
 * sends message to every mentioned and connected recipient, or if none is
 * specified, to all modules. That's communication model.
 * </p>
 * 
 * <p>
 * <b>Mockups</b> are structures containing model information as attachments to
 * events. Model classes are used as model and are not ment to be serialized.
 * </p>
 * 
 * 
 * SCZR 2014Z course project
 * 
 * @author Rafał Braun, Kamil Drożdżał, Amadeusz Sadowski, Mikołaj Sowiński,
 *         Piotr Suchorab
 *
 */
package burtis;