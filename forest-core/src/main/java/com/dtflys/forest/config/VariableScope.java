package com.dtflys.forest.config;

import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.ForestVariableValue;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 14:44
 */
public interface VariableScope {

    boolean isVariableDefined(String name);

    Object getVariableValue(String name, VariableScope variableScope);

    Object getVariableValue(String name);

    ForestVariableValue getVariable(String name);

    ForestMethod getForestMethod();

    ForestConfiguration getConfiguration();


}
