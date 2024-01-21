package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.ForestVariableValue;

/**
 * Created by Administrator on 2016/5/4.
 */
public class MappingVariable extends MappingParameter implements ForestVariableValue {

    public MappingVariable(String name, Class type) {
        super(type);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return "[Variable name: " + name + "]";
    }

    @Override
    public Object getValue(VariableScope variableScope) {
        if (variableScope instanceof ForestRequestContext) {
            ForestRequestContext requestContext = (ForestRequestContext) variableScope;
            return requestContext.getArgument(index);
        }
        return null;
    }
}
