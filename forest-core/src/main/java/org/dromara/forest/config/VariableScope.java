package org.dromara.forest.config;

import org.dromara.forest.mapping.MappingVariable;
import org.dromara.forest.reflection.ForestMethod;

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
