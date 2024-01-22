package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.reflection.ForestVariableValue;

public class ForestRequestContext extends ForestVariableContext {

    private final Object[] arguments;

    public ForestRequestContext(VariableScope parent, Object[] arguments) {
        super(parent);
        this.arguments = arguments;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public Object getArgument(int index) {
        if (arguments == null) {
            return null;
        }
        return arguments[index];
    }

    @Override
    public Object getVariableValue(String name) {
        Object value = variables.get(name);
        if (value == null) {
            value = parent.getVariableValue(name, this);
        }
        if (value instanceof MappingVariable) {
            return getArgument(((MappingVariable) value).index);
        }
        if (value instanceof ForestVariableValue) {
            return ((ForestVariableValue) value).getValue(this);
        }
        return value;

    }
}
