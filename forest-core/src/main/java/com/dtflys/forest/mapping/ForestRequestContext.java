package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;

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
}
