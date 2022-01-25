package ru.otus.cachehw;


import java.lang.ref.WeakReference;
import java.util.*;

public class MyCache<K, V> implements HwCache<K, V> {
//Надо реализовать эти методы
    private final Map<K, V> cache = new WeakHashMap<>();
    private final List<WeakReference<HwListener<K,V>>> listeners = new ArrayList<>();

    private static final String ACTION_PUT = "put value";
    private static final String ACTION_REMOVE = "remove value";
    private static final String ACTION_GET = "get value";

    @Override
    public void put(K key, V value) {
        cache.put(key,value);
        listeners.forEach(listener->listener.get().notify(key,value,ACTION_PUT));
    }

    @Override
    public void remove(K key) {
        V value = cache.get(key);
        cache.remove(key,value);
        listeners
                .stream()
                .filter(l->Optional.ofNullable(l).isPresent())
                .forEach(listener->listener.get().notify(key,value,ACTION_REMOVE));
    }

    @Override
    public V get(K key) {
        listeners
                .stream()
                .filter(l->Optional.ofNullable(l).isPresent())
                .forEach(listener->listener.get().notify(key,cache.get(key),ACTION_GET));
        return cache.get(key);
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(new WeakReference<HwListener<K,V>>(listener));
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }
}
