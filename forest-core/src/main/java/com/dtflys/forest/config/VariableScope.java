package com.dtflys.forest.config;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.ForestVariable;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 14:44
 */
public interface VariableScope {

    boolean isVariableDefined(String name);
    
    default Object variableValue(String name) {
        return getVariableValue(name);
    }

    default <R> R variableValue(String name, Class<R> clazz) {
        return getVariableValue(name, clazz);
    }

    Object getVariableValue(String name);

    <R> R getVariableValue(String name, Class<R> clazz);

    Object getVariableValue(String name, ForestRequest request);

    <R> R getVariableValue(String name, ForestRequest request, Class<R> clazz);

    ForestVariable getVariable(String name);

    ForestConfiguration getConfiguration();

}
