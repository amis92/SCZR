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
    
    public static final int BUS_CAPACITY = 50;
    public static final int NUMBER_OF_BUSES = 10;
    public static final int BUS_SPEED = 15;
    public static final int BUS_MAX_CYCLES = 4;
    public static final int TERMINUS_RELEASING_FREQUENCY = 20;
    
    public static List<Entry<Integer,String>> getDefaultBusStops() {
        
        List<Entry<Integer,String>> busStops = new LinkedList<>();
        
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(30, "Żerań"));
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(60, "Dw. Gdański"));
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(90, "Centrum"));
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(120, "Filtry"));
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(150, "Centrum"));
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(180, "Rondo ONZ"));
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(210, "Złota"));
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(240, "GUS"));
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(270, "Och-Teatr"));
        // This will be TERMINUS!
        busStops.add(new AbstractMap.SimpleEntry<Integer, String>(300, "Banacha"));
        
        return busStops;
        
    }
    
    public static int getLineLength() {
        List<Entry<Integer,String>> busStops = getDefaultBusStops();
        return busStops.get(busStops.size()-1).getKey();
    }
    
}
