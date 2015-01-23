/**
 * This module controls time flow of simulation, dictating clock ticks to all
 * other modules. Additionally, it checks whether modules described as critical
 * don't lag behind - if that's the case, simulation is terminated immediately.
 * 
 * @author Mikołaj Sowiński
 * @author Amadeusz Sadowski
 *
 */
package burtis.modules.sync;