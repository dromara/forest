package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.exceptions.ForestVariableUndefinedException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.reflection.ForestArgumentsVariable;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.ForestVariable;
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


    public MappingReference(ForestMethod<?> forestMethod, VariableScope variableScope, String name, int startIndex, int endIndex) {
        super(forestMethod, Token.REF);
        this.variableScope = variableScope;
        this.name = name;
        setIndexRange(startIndex, endIndex);
    }

    public String getName() {
        return name;
    }

    @Override
    public Object render(ForestRequest request, Object[] args) {
        ForestVariable variable = variableScope.getVariable(name);
        if (variable != null) {
            if (variable instanceof ForestArgumentsVariable) {
                return ((ForestArgumentsVariable) variable).getValue(request, args);
            }
            return variable.getValue(request);
        }
        if (!variableScope.isVariableDefined(name)) {
            throw new ForestVariableUndefinedException(name, startIndex, endIndex);
        }
        return variableScope.getVariableValue(name, request);
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
