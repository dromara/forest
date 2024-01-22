package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.config.VariableValueContext;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.reflection.ForestMethod;

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

    public abstract boolean isIterateVariable();


}
