package com.dtflys.forest.reflection;

import com.dtflys.forest.config.VariableValueContext;
import com.dtflys.forest.http.Lazy;

public class SimpleVariable implements ForestVariableDef {

    private final Object value;

    public SimpleVariable(Object value) {
        this.value = value;
    }

    @Override
    public Object getValue(VariableValueContext valueContext) {
        if (value instanceof Lazy) {
            return ((Lazy<?>) value).eval(valueContext.getRequest());
        }
        return this.value;
    }
}
