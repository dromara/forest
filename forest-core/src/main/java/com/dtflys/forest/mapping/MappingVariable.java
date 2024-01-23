package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableValueContext;
import com.dtflys.forest.reflection.ForestVariableDef;

/**
 * Created by Administrator on 2016/5/4.
 */
public class MappingVariable extends MappingParameter implements ForestVariableDef {

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
    public Object getValue(VariableValueContext valueContext) {
        if (index != null && index > -1) {
            return valueContext.getArgument(index);
        }
        return null;
    }
}
