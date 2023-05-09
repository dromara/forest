package org.dromara.forest.http.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ForestModel {

    private final List<Object> models = new LinkedList<>();

    private final Map<String, JavaObjectProperty> properties = new HashMap<>();

    public List<Object> getModels() {
        return models;
    }

    public Map<String, JavaObjectProperty> getProperties() {
        return properties;
    }
}
