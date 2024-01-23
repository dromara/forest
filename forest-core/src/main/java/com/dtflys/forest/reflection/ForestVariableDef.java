package com.dtflys.forest.reflection;

import com.dtflys.forest.config.VariableValueContext;

/**
 * Forest 变量接口
 */
@FunctionalInterface
public interface ForestVariableDef {

    Object getValue(VariableValueContext valueContext);

    static ForestVariableDef fromObject(final Object value) {
        if (value instanceof ForestVariableDef) {
            return (ForestVariableDef) value;
        }
        return new BasicVariableDef(value);
    }


}
