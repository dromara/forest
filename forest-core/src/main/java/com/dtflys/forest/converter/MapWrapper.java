package com.dtflys.forest.converter;

import com.dtflys.forest.callback.Lazy;
import com.dtflys.forest.http.ForestRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MapWrapper<K> implements Map<K, Object> {

    private final ForestRequest request;
    private final Map map;

    private final int target;

    public MapWrapper(ForestRequest request, Map<K, Object> map, int target) {
        this.request = request;
        this.map = map;
        this.target = target;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        Object obj = map.get(key);
        if (obj instanceof Lazy) {
            return new LazyWrapper<>(request, String.valueOf(key), (Lazy<Object>) obj, target);
        }
        return obj;
    }

    @Nullable
    @Override
    public Object put(K key, Object value) {
        return map.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ?> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @NotNull
    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @NotNull
    @Override
    public Set<Entry<K, Object>> entrySet() {
        return map.entrySet();
    }

}
