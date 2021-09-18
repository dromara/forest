package com.dtflys.forest.mapping;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.reflection.ForestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * 子变量作用域
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA9
 */
public class SubVariableScope implements VariableScope {

    private final VariableScope parent;

    private final Map<String, Object> variables = new HashMap<>();

    public SubVariableScope(VariableScope parent) {
        this.parent = parent;
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
        return getVariableValue(name, null);
    }

    @Override
    public Object getVariableValue(String name, ForestMethod method) {
        Object value = variables.get(name);
        if (value == null) {
            return parent.getVariableValue(name, method);
        }
        return value;
    }

    public void addVariableValue(String name, Object value) {
        variables.put(name, value);
    }

    @Override
    public MappingVariable getVariable(String name) {
        return null;
    }

    @Override
    public ForestConfiguration getConfiguration() {
        return parent.getConfiguration();
    }
}
