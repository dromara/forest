package com.dtflys.forest.reflection;

import com.dtflys.forest.config.VariableScope;

/**
 * Forest 变量接口
 */
public interface ForestVariableValue {

    Object getValue(VariableScope variableScope);

    static ForestVariableValue fromObject(final Object value) {
        if (value instanceof ForestVariableValue) {
            return (ForestVariableValue) value;
        }
        return new BasicVariableValue(value);
    }


}
