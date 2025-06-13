package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.exceptions.ForestReferenceException;
import com.dtflys.forest.http.RequestVariableScope;
import com.dtflys.forest.reflection.ForestArgumentsVariable;
import com.dtflys.forest.reflection.ForestVariable;
import com.dtflys.forest.utils.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2016/5/26.
 */
public class MappingReference extends MappingExpr {

    final boolean optional;
    private final String name;

    private final static Set<String> ITERATE_VARS = new HashSet<>();
    static {
        ITERATE_VARS.add("_index");
        ITERATE_VARS.add("_key");
    }

    public MappingReference(MappingTemplate source, String name, int startIndex, int endIndex) {
        this(source, name, false, startIndex, endIndex);
    }

    public MappingReference(MappingTemplate source, String name, boolean optional, int startIndex, int endIndex) {
        super(source, Token.REF);
        this.name = name;
        this.optional = optional;
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
            try {
                if (variable instanceof ForestArgumentsVariable) {
                    if (scope instanceof RequestVariableScope) {
                        return checkDeepReference(((ForestArgumentsVariable) variable).getValueFromScope(scope, args),
                                this, scope, args);
                    }
                }
                return checkDeepReference(variable.getValueFromScope(scope), this, scope, args);
            } catch (Throwable th) {
                throwReferenceException(this, th);
            }
        }
        if (optional) {
            return MappingEmpty.OPTIONAL;
        }
        throwVariableUndefinedException(name);
        return null;
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
