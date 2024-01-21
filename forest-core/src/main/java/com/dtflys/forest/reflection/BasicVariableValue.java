package com.dtflys.forest.reflection;

import com.dtflys.forest.config.VariableScope;

public class BasicVariableValue implements ForestVariableValue {

    private final Object value;

    public BasicVariableValue(Object value) {
        this.value = value;
    }


    @Override
    public Object getValue(VariableScope variableScope) {
        return this.value;
    }
}
