package com.dtflys.forest.http.model;

import java.util.HashMap;
import java.util.Map;

public class ForestModelWrapper {
    private final Object model;

    private final Map<String, ForestModelProperty> properties = new HashMap<>();

    public ForestModelWrapper(Object model) {
        this.model = model;
    }


}
