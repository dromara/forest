package com.dtflys.forest.springboot.properties;

import com.dtflys.forest.converter.json.ForestJsonConverter;

import java.util.HashMap;
import java.util.Map;

public class ForestConverterItemProperties {

    private Class<? extends ForestJsonConverter> type;

    private Map<String, Object> parameters = new HashMap<>();

    public Class<? extends ForestJsonConverter> getType() {
        return type;
    }

    public void setType(Class<? extends ForestJsonConverter> type) {
        this.type = type;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
