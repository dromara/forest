package com.dtflys.forest.mapping;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.config.VariableValueContext;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.ForestVariableDef;

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

    protected final Map<String, ForestVariableDef> variables = new HashMap<>();

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
    public boolean isVarDefined(String name) {
        boolean isDefined = variables.containsKey(name);
        if (!isDefined) {
            return parent.isVarDefined(name);
        }
        return true;
    }

    @Override
    public Object getVar(String name, VariableValueContext valueContext) {
        Object value = variables.get(name);
        if (value == null && parent != null) {
            value = parent.getVar(name, valueContext);
        }
        if (value instanceof ForestVariableDef) {
            return ((ForestVariableDef) value).getValue(valueContext);
        }
        return value;
    }

    public void setVar(String name, Object value) {
        variables.put(name, ForestVariableDef.fromObject(value));
    }

    @Override
    public ForestVariableDef getVarDef(String name) {
        final ForestVariableDef variableValue = variables.get(name);
        if (variableValue == null) {
            return parent.getVarDef(name);
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
