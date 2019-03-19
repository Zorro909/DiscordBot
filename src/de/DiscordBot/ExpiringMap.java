package de.DiscordBot;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ExpiringMap<K, V> implements Map<K, V> {

    private HashMap<K, Object[]> internalMap = new HashMap<>();

    private long nextToClear = Long.MAX_VALUE;

    long defaultLifetime = 1000;

    public ExpiringMap() {
    }

    public ExpiringMap(long defaultLifetime) {
        this.defaultLifetime = defaultLifetime;
    }

    @Override
    public void clear() {
        internalMap.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        checkClearance(5);
        return internalMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        checkClearance(5);
        return internalMap.containsValue(value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        checkClearance(5);
        HashSet<Entry<K, V>> entries = new HashSet<>(internalMap.size());
        Iterator<K> iterator = internalMap.keySet().iterator();
        while (iterator.hasNext()) {
            Entry<K, V> entry = new ExpiringEntry<>(internalMap, iterator.next());
            entries.add(entry);
        }
        return entries;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object key) {
        checkClearance(5);
        return (V) internalMap.get(key)[0];
    }

    @Override
    public boolean isEmpty() {
        checkClearance(5);
        return internalMap.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        checkClearance(5);
        return internalMap.keySet();
    }

    @Override
    public V put(K key, V value) {
        checkClearance(5);
        internalMap.put(key, new Object[] { value, defaultLifetime });
        return value;
    }

    public V put(K key, V value, long lifetime) {
        checkClearance(5);
        internalMap.put(key, new Object[] { value, lifetime });
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        checkClearance(5);
        m.forEach((key, value) -> {
            put(key, value);
        });
    }

    public void putAll(Map<? extends K, ? extends V> m, long lifetime) {
        checkClearance(5);
        m.forEach((key, value) -> {
            put(key, value, lifetime);
        });
    }

    @Override
    public V remove(Object key) {
        checkClearance(5);
        Object[] array = internalMap.remove(key);
        if (array != null) {
            return (V) array[0];
        }
        return null;
    }

    @Override
    public int size() {
        checkClearance(5);
        return internalMap.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<V> values() {
        return (Collection<V>) internalMap.values().stream().map((Object[] obj) -> obj[0]).collect(Collectors.toList());
    }

    private void checkClearance(int offsetInMS) {
        long currentTime = System.currentTimeMillis() + offsetInMS;
        if (nextToClear < currentTime) {
            nextToClear = Long.MAX_VALUE;
            checkDeepClearance(currentTime);
        }
    }

    private void checkDeepClearance(long currentTime) {
        synchronized (internalMap) {
            Iterator<K> iterator = internalMap.keySet().iterator();
            while (iterator.hasNext()) {
                long expiration = (long) internalMap.get(iterator.next())[1];
                if (expiration < currentTime) {
                    iterator.remove();
                } else {
                    if (expiration < nextToClear) {
                        nextToClear = expiration;
                    }
                }
            }
        }
    }

    class ExpiringEntry<K, V> implements Map.Entry<K, V> {

        HashMap<K, Object[]> internalMap;
        K key;

        public ExpiringEntry(HashMap<K, Object[]> internalMap, K key) {
            this.internalMap = internalMap;
            this.key = key;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return (V) internalMap.get(key)[0];
        }

        @Override
        public V setValue(V value) {
            Object[] array = internalMap.get(key);
            array[0] = value;
            internalMap.put(key, array);
            return value;
        }

        public long clearTime() {
            return (long) internalMap.get(key)[1];
        }

    }

}
