package model;

/**
* Created by mateusz on 27.05.14.
*/
public class Pair<K, V> {
    private K k;
    private V v;
    public Pair(K kk, V vv){
        k = kk;
        v = vv;
    }
    public V getValue(){
        return v;
    }
    public K getKey(){
        return k;
    }
}
