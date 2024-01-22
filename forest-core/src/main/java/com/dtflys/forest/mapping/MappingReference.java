package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.config.VariableValueContext;
import com.dtflys.forest.exceptions.ForestVariableUndefinedException;
import com.dtflys.forest.http.ForestRequest;
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

    private boolean nullable;

    private final static Set<String> ITERATE_VARS = new HashSet<>();
    static {
        ITERATE_VARS.add("_index");
        ITERATE_VARS.add("_key");
    }

    public MappingReference(String name, boolean nullable) {
        super(Token.REF);
        this.name = name;
        this.nullable = nullable;
    }

    public String getName() {
        return name;
    }

    @Override
    public Object render(VariableValueContext valueContext) {
        Object result = valueContext.getVariableValue(name);
        if (result == null) {
            if (nullable) {
                return null;
            }
            throw new ForestVariableUndefinedException(getName());
        }
        return result;
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
