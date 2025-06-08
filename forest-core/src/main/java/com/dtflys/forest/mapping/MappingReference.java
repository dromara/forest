package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.exceptions.ForestVariableUndefinedException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.RequestVariableScope;
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


    public MappingReference(ForestMethod<?> forestMethod, String name, int startIndex, int endIndex) {
        super(forestMethod, Token.REF);
        this.name = name;
        setIndexRange(startIndex, endIndex);
    }

    public String getName() {
        return name;
    }

    @Override
    public Object render(VariableScope scope, Object[] args) {
        ForestVariable variable = null;
        if (scope != null) {
            variable = scope.getVariable(name);
        }
        if (variable != null) {
            if (variable instanceof ForestArgumentsVariable) {
                if (scope instanceof RequestVariableScope) {
                    return ((ForestArgumentsVariable) variable).getValueFromScope(scope, args);
                }
            }
            return variable.getValueFromScope(scope);
        }
        throw new ForestVariableUndefinedException(name, startIndex, endIndex);
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
