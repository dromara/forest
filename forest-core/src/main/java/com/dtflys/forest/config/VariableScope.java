package com.dtflys.forest.config;

import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.ForestVariableDef;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 14:44
 */
public interface VariableScope {

    boolean isVarDefined(String name);

    Object getVariable(String name, VariableValueContext valueContext);

    ForestVariableDef getVariableDef(String name);

    ForestMethod getForestMethod();

    ForestConfiguration getConfiguration();


}
