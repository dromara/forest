package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.reflection.ForestMethod;

public class MappingElvisExpr extends MappingExpr {

    private final MappingExpr left;

    private final MappingExpr right;

    protected MappingElvisExpr(ForestMethod forestMethod, MappingExpr left, MappingExpr right, int startIndex, int endIndex) {
        super(forestMethod, Token.ELVIS);
        this.left = left;
        this.right = right;
        setIndexRange(startIndex, endIndex);
    }

    @Override
    public boolean isIterateVariable() {
        return false;
    }

    @Override
    public Object render(VariableScope scope, Object[] args) {
        Object obj = left.render(scope, args);
        if (obj == null || obj instanceof MappingEmpty) {
            return right.render(scope, args);
        }
        return obj;
    }
}
