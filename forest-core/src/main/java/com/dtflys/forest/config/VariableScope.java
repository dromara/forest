package com.dtflys.forest.config;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.BasicVariable;
import com.dtflys.forest.reflection.ForestArgumentsVariable;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.ForestVariable;
import com.dtflys.forest.reflection.TemplateVariable;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 14:44
 */
public interface VariableScope<SELF extends VariableScope<SELF>> {

    boolean isVariableDefined(String name);
    
    default Object variableValue(String name) {
        return getVariableValue(name);
    }

    default <R> R variableValue(String name, Class<R> clazz) {
        return getVariableValue(name, clazz);
    }

    <R> R getVariableValue(String name);

    <R> R getVariableValue(String name, Class<R> clazz);

    Object getVariableValue(String name, ForestRequest request);

    <R> R getVariableValue(String name, ForestRequest request, Class<R> clazz);

    ForestVariable getVariable(String name);

    ForestConfiguration getConfiguration();

    SELF setVariable(String name, ForestVariable variable);

    SELF setVariable(String name, ForestArgumentsVariable variable);

    SELF setVariable(String name, Object value);

    SELF var(String name, ForestVariable variable);

    SELF var(String name, ForestArgumentsVariable variable);

    SELF var(String name, Object value);

    Object var(String name);

    default <R> R varAs(String name, ForestRequest request, Class<R> clazz) {
        return getVariableValue(name, request, clazz);
    }

    default <R> R varAs(String name, Class<R> clazz) {
        return getVariableValue(name, clazz);
    }

    default String varAsString(String name) {
        return varAs(name, String.class);
    }

}
