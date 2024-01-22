package com.dtflys.forest.mapping;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.reflection.BasicVariableValue;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.ForestVariableValue;

import java.util.HashMap;
import java.util.Map;

/**
 * 子变量作用域
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA9
 */
public class ForestVariableContext implements VariableScope {

    protected final VariableScope parent;

    protected final ForestConfiguration configuration;

    protected final Map<String, ForestVariableValue> variables = new HashMap<>();

    public ForestVariableContext(VariableScope parent, ForestConfiguration configuration) {
        this.parent = parent;
        this.configuration = configuration;
    }

    public ForestVariableContext(VariableScope parent) {
        this(parent, null);
    }


    public ForestVariableContext() {
        this(null, null);
    }


    @Override
    public boolean isVariableDefined(String name) {
        boolean isDefined = variables.containsKey(name);
        if (!isDefined) {
            return parent.isVariableDefined(name);
        }
        return true;
    }

    @Override
    public Object getVariableValue(String name, VariableScope variableScope) {
        Object value = variables.get(name);
        if (value == null && parent != null) {
            value = parent.getVariableValue(name, variableScope);
        }
        if (value instanceof ForestVariableValue) {
            return ((ForestVariableValue) value).getValue(variableScope);
        }
        return value;
    }

    @Override
    public Object getVariableValue(String name) {
        return getVariableValue(name, this);
    }

    public void setVariableValue(String name, Object value) {
        variables.put(name, ForestVariableValue.fromObject(value));
    }

    @Override
    public ForestVariableValue getVariable(String name) {
        final ForestVariableValue variableValue = variables.get(name);
        if (variableValue == null) {
            return parent.getVariable(name);
        }
        return variableValue;
    }

    @Override
    public ForestMethod getForestMethod() {
        if (parent != null) {
            return parent.getForestMethod();
        }
        return null;
    }

    @Override
    public ForestConfiguration getConfiguration() {
        if (configuration != null) {
            return configuration;
        }
        if (parent != null) {
            return parent.getConfiguration();
        }
        return null;
    }
}
