package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableValueContext;

/**
 * @author gongjun
 * @since 2016-05-04
 */
public abstract class MappingExpr {

    final Token token;


    protected MappingExpr(Token token) {
        this.token = token;
    }

    public Object render(VariableValueContext valueContext) {
        return null;
    }

    public Object render(VariableValueContext valueContext, boolean allowUndefinedVariable) {
        return render(valueContext);
    }


    public abstract boolean isIterateVariable();


}
