package com.dtflys.forest.reflection;

import com.dtflys.forest.config.VariableValueContext;

public class BasicVariableDef implements ForestVariableDef {

    private final Object value;

    public BasicVariableDef(Object value) {
        this.value = value;
    }


    @Override
    public Object getValue(VariableValueContext valueContext) {
        return this.value;
    }
}
