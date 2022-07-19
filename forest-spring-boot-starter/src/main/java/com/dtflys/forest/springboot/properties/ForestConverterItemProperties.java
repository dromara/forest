package com.dtflys.forest.springboot.properties;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.ForestJsonConverter;

import java.util.HashMap;
import java.util.Map;

public class ForestConverterItemProperties {

    private Class<? extends ForestConverter> type;

    private Map<String, Object> parameters = new HashMap<>();

    public Class<? extends ForestConverter> getType() {
        return type;
    }

    public void setType(Class<? extends ForestConverter> type) {
        this.type = type;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
