package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.reflection.BasicVariable;
import com.dtflys.forest.reflection.ForestArgumentsVariable;
import com.dtflys.forest.reflection.ForestVariable;
import com.dtflys.forest.reflection.TemplateVariable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractVariableScope<SELF extends VariableScope> implements VariableScope {

    protected final VariableScope parent;

    protected final Map<String, ForestVariable> variables = new ConcurrentHashMap<>();

    public AbstractVariableScope(VariableScope parent) {
        this.parent = parent;
    }
    

    @Override
    public boolean isVariableDefined(String name) {
        boolean isDefined = variables.containsKey(name);
        if (!isDefined && parent != null) {
            return parent.isVariableDefined(name);
        }
        return true;
    }

    @Override
    public Object getVariableValue(String name) {
        return getVariableValue(name, (ForestRequest) null);
    }

    @Override
    public <R> R getVariableValue(String name, Class<R> clazz) {
        return getVariableValue(name, null, clazz);
    }

    @Override
    public Object getVariableValue(String name, ForestRequest request) {
        ForestVariable variable = getVariable(name);
        if (variable == null) {
            return null;
        }
        return variable.getValue(request);
    }

    @Override
    public <R> R getVariableValue(String name, ForestRequest request, Class<R> clazz) {
        ForestVariable variable = getVariable(name);
        if (variable == null) {
            return null;
        }
        Object result = variable.getValue(request);
        if (result == null) {
            return null;
        }
        return clazz.cast(result);
    }

    @Override
    public ForestVariable getVariable(String name) {
        ForestVariable variable = variables.get(name);
        if (variable == null && parent != null) {
            return parent.getVariable(name);
        }
        return variable;
    }

    public SELF setVariable(String name, ForestVariable variable) {
        variables.put(name, variable);
        return (SELF) this;
    }

    public SELF setVariable(String name, ForestArgumentsVariable variable) {
        variables.put(name, variable);
        return (SELF) this;
    }
    
    public SELF setVariable(String name, Object value) {
        if (value instanceof ForestVariable) {
            variables.put(name, (ForestVariable) value);
        } else if (value instanceof CharSequence) {
            final MappingTemplate mappingTemplate = MappingTemplate.create(this, String.valueOf(value)); 
            final TemplateVariable templateVariable = new TemplateVariable(mappingTemplate);
            variables.put(name, templateVariable);
        } else {
            variables.put(name, new BasicVariable(value));
        }
        return (SELF) this;
    }
    
}
