package com.dtflys.forest.reflection;

import com.dtflys.forest.http.ForestRequest;

public class BasicVariable implements ForestVariable {

    private final Object value;

    public BasicVariable(Object value) {
        this.value = value;
    }


    @Override
    public Object getValue(ForestRequest request) {
        return value;
    }
    
    public Object getValue() {
        return value;
    }
}
