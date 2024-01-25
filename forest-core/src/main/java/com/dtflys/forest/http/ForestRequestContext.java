package com.dtflys.forest.http;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.config.VariableValueContext;
import com.dtflys.forest.http.ForestQueryMap;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.mapping.ForestVariableContext;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.ForestVariableDef;

public class ForestRequestContext extends ForestVariableContext implements VariableValueContext {

    private final Object[] arguments;

    ForestRequest<?> request;

    public ForestRequestContext(VariableScope parent, Object[] arguments) {
        super(parent);
        this.arguments = arguments;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Object getArgument(int index) {
        if (arguments == null) {
            return null;
        }
        return arguments[index];
    }

    @Override
    public ForestQueryMap getQuery() {
        return null;
    }

    @Override
    public ForestRequest<?> getRequest() {
        return request;
    }

    @Override
    public Object getVariable(String name, VariableValueContext valueContext) {
        Object value = variables.get(name);
        if (value == null) {
            value = parent.getVariable(name, valueContext);
        }
        if (value instanceof MappingVariable) {
            return getArgument(((MappingVariable) value).getIndex());
        }
        if (value instanceof ForestVariableDef) {
            return ((ForestVariableDef) value).getValue(valueContext);
        }
        return value;
    }
}
