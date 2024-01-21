package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.exceptions.ForestVariableUndefinedException;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.ForestVariableValue;
import com.dtflys.forest.utils.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2016/5/26.
 */
public class MappingReference extends MappingExpr {

    private String name;

    private final static Set<String> ITERATE_VARS = new HashSet<>();
    static {
        ITERATE_VARS.add("_index");
        ITERATE_VARS.add("_key");
    }

    public MappingReference(VariableScope variableScope, String name) {
        super(Token.REF);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Object render(VariableScope variableScope, Object[] args) {
        ForestVariableValue variable = variableScope.getVariable(name);
        if (variable instanceof MappingVariable) {
            MappingVariable mappingVariable = (MappingVariable) variable;
            return args[mappingVariable.getIndex()];
        } else if (variable != null) {
            return variable.getValue(variableScope);
        }
        if (!variableScope.isVariableDefined(name)) {
            throw new ForestVariableUndefinedException(name);
        }
        return variableScope.getVariableValue(name);
    }

    @Override
    public boolean isIterateVariable() {
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        if (ITERATE_VARS.contains(name)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "[Refer: " + name + "]";
    }
}
