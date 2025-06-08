package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.reflection.ForestMethod;

/**
 * @author gongjun
 * @since 2016-05-04
 */
public abstract class MappingExpr {

    protected final ForestMethod<?> forestMethod;

    final Token token;

    int startIndex = -1;

    int endIndex = -1;

    protected MappingExpr(ForestMethod<?> forestMethod, Token token) {
        this.forestMethod = forestMethod;
        this.token = token;
    }

    public Object render(VariableScope scope, Object[] args) {
        return null;
    }

    public Object render(VariableScope scope) {
        return render(scope, new Object[0]);
    }


    public abstract boolean isIterateVariable();

    public void setIndexRange(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }


}
