package ru.otus.cachehw;


import java.lang.ref.WeakReference;
import java.util.*;

public class MyCache<K, V> implements HwCache<K, V> {
//Надо реализовать эти методы
    private final Map<K, V> cache = new WeakHashMap<>();
    private final List<HwListener<K,V>> listeners = new ArrayList<>();

    private static final String ACTION_PUT = "put value";
    private static final String ACTION_REMOVE = "remove value";

    @Override
    public void put(K key, V value) {
        cache.put(key,value);
        listeners.forEach(listener->notify(listener,key,value,ACTION_PUT));
    }

    @Override
    public void remove(K key) {
        V value = cache.get(key);
        cache.remove(key,value);
        listeners
                .stream()
                .filter(l->Optional.ofNullable(l).isPresent())
                .forEach(listener->notify(listener,key,value,ACTION_REMOVE));
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    private void notify(HwListener listener, K key, V value, String action) {
        try {
            listener.notify(key,value,action);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
