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

    private final VariableScope parent;

    private final ForestConfiguration configuration;

    private final Map<String, ForestVariableValue> variables = new HashMap<>();

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
    public Object getVariableValue(String name) {
        Object value = variables.get(name);
        if (value == null) {
            return parent.getVariableValue(name);
        }
        return value;
    }

    public void setVariableValue(String name, Object value) {
        variables.put(name, new BasicVariableValue(value));
    }

    @Override
    public ForestVariableValue getVariable(String name) {
        return variables.get(name);
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
