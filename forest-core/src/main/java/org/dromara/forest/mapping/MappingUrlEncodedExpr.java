package org.dromara.forest.mapping;

import org.dromara.forest.config.VariableScope;
import org.dromara.forest.reflection.ForestMethod;

public class MappingUrlEncodedExpr extends MappingExpr {

    private final MappingExpr expr;

    protected MappingUrlEncodedExpr(ForestMethod<?> forestMethod, MappingExpr expr) {
        super(forestMethod, expr.token);
        this.expr = expr;
    }

    @Override
    public void setVariableScope(VariableScope variableScope) {
        super.setVariableScope(variableScope);
        expr.setVariableScope(variableScope);
    }

    @Override
    public boolean isIterateVariable() {
        return expr.isIterateVariable();
    }

    public MappingExpr getExpr() {
        return expr;
    }

    @Override
    public Object render(Object[] args) {
        Object ret = expr.render(args);
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
