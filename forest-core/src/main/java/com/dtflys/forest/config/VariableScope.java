package com.dtflys.forest.config;

import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.ForestMethod;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 14:44
 */
public interface VariableScope {

    boolean isVariableDefined(String name);

    Object getVariableValue(String name);

    Object getVariableValue(String name, ForestMethod method);

    MappingVariable getVariable(String name);

    ForestConfiguration getConfiguration();
}
