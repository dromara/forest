package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.config.VariableScope;

/**
 * @author gongjun
 * @since 2016-05-04
 */
public abstract class MappingExpr {

    final Token token;

    protected VariableScope variableScope;

    protected MappingExpr(Token token) {
        this.token = token;
    }

    public Object render(Object[] args) {
        return null;
    }
}
