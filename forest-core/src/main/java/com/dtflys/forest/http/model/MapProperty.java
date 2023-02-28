package com.dtflys.forest.http.model;

import com.dtflys.forest.converter.ConvertOptions;
import com.dtflys.forest.http.ForestRequest;

import java.util.Map;

public class MapProperty implements ObjectProperty {

    private final Map map;

    private final String key;

    public MapProperty(Map map, String key) {
        this.map = map;
        this.key = key;
    }

    @Override
    public Object getInstance() {
        return map;
    }

    @Override
    public String getName() {
        return key;
    }

    @Override
    public Object getValue(ForestRequest request, ConvertOptions options) {
        return map.get(key);
    }
}
