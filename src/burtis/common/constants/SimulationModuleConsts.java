/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burtis.common.constants;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author Mikołaj Sowiński
 */
public class SimulationModuleConsts {
    
    public static final int BUS_CAPACITY = 70;
    public static final int NUMBER_OF_BUSES = 10;
    public static final int BUS_SPEED = 3;
    public static final int BUS_START_INTERVAL = 300;
    public static final int BUS_MAX_CYCLES = 4;
    public static final int TERMINUS_RELEASING_FREQUENCY = 20;
    
    public static List<Entry<Integer,String>> getDefaultBusStops() {
        
        List<Entry<Integer,String>> busStops = new LinkedList<>();
        
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(30, "Plac Zamkowy"));
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(60, "Hotel Bristol"));
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(90, "Uniwersytet"));
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(120, "Ordynacka"));
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(150, "Foksal"));
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(180, "Plac Trzech Krzyży"));
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(210, "Plac na Rozdrożu"));
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(240, "Plac Unii Lubelskiej"));
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(270, "Rakowiecka"));
        // This will be TERMINUS!
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(300, "Bielańska"));
        
        return busStops;
        
    }
    
}
