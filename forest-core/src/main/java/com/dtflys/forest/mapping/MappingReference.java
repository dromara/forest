package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;

/**
 * Created by Administrator on 2016/5/26.
 */
public class MappingReference extends MappingExpr {

    private String name;

    public MappingReference(VariableScope variableScope, String name) {
        super(Token.REF);
        this.variableScope = variableScope;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Object render(Object[] args) {
        MappingVariable variable = variableScope.getVariable(name);
        if (variable != null) {
            return args[variable.getIndex()];
        }
        return variableScope.getVariableValue(name);
    }

    @Override
    public String toString() {
        return "[Refer: " + name + "]";
    }
}
