package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.RequestVariableScope;
import com.dtflys.forest.reflection.ForestMethod;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-09 17:49
 */
public class MappingBoolean extends MappingExpr {

    private boolean value;

    protected MappingBoolean(boolean value, int startIndex, int endIndex) {
        super(null, Token.BOOLEAN);
        this.value = value;
        setIndexRange(startIndex, endIndex);
    }

    public boolean isValue() {
        return value;
    }

    @Override
    public Object render(VariableScope scope, Object[] args) {
        return value;
    }

    @Override
    public boolean isIterateVariable() {
        return false;
    }

    @Override
    public String toString() {
        return "[BOOL: " + value + "]";
    }
}
