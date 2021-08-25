package com.dtflys.forest.reflection;

public class BasicVariableValue implements ForestVariableValue {

    private final Object value;

    public BasicVariableValue(Object value) {
        this.value = value;
    }


    @Override
    public Object getValue() {
        return this.value;
    }
}
