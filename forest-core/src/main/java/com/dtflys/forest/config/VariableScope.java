package com.dtflys.forest.config;

import com.dtflys.forest.mapping.MappingVariable;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 14:44
 */
public interface VariableScope {

    boolean isVariableDefined(String name);

    Object getVariableValue(String name);

    MappingVariable getVariable(String name);

    ForestConfiguration getConfiguration();
}
