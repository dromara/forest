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

    protected VariableScope variableScope;

    int startIndex = -1;

    int endIndex = -1;

    protected MappingExpr(ForestMethod<?> forestMethod, Token token) {
        this.forestMethod = forestMethod;
        this.token = token;
    }

    public Object render(Object[] args) {
        return null;
    }

    public void setVariableScope(VariableScope variableScope) {
        this.variableScope = variableScope;
    }

    public abstract boolean isIterateVariable();

    public void setIndexRange(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }


}
