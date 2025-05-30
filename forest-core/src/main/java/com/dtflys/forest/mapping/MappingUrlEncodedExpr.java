package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.RequestVariableScope;
import com.dtflys.forest.reflection.ForestMethod;

public class MappingUrlEncodedExpr extends MappingExpr {

    private final MappingExpr expr;

    protected MappingUrlEncodedExpr(ForestMethod<?> forestMethod, MappingExpr expr) {
        super(forestMethod, expr.token);
        this.expr = expr;
    }


    @Override
    public boolean isIterateVariable() {
        return expr.isIterateVariable();
    }

    public MappingExpr getExpr() {
        return expr;
    }

    @Override
    public Object render(RequestVariableScope scope, Object[] args) {
        Object ret = expr.render(scope, args);
        if (ret == null) {
            return null;
        }
        String str = String.valueOf(ret);
        return str;
    }

    @Override
    public String toString() {
        return "[Encode: " + expr.toString() + "]";
    }
}
