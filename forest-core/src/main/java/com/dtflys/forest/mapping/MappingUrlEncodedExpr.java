package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableValueContext;

public class MappingUrlEncodedExpr extends MappingExpr {

    private final MappingExpr expr;

    protected MappingUrlEncodedExpr(MappingExpr expr) {
        super(expr.token);
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
    public Object render(VariableValueContext valueContext) {
        Object ret = expr.render(valueContext);
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
