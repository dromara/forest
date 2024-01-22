package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.config.VariableValueContext;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.reflection.ForestMethod;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-09 17:49
 */
public class MappingLong extends MappingExpr {

    private final long number;

    public MappingLong(long number) {
        super(Token.LONG);
        this.number = number;
    }

    @Override
    public Object render(VariableValueContext valueContext) {
        return number;
    }

    @Override
    public boolean isIterateVariable() {
        return false;
    }

    public long getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "[Long: " + number + "]";
    }

}
