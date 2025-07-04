package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;

public class MappingElvisExpr extends MappingExpr {

    private final MappingExpr left;

    private final MappingExpr right;

    protected MappingElvisExpr(MappingTemplate source, MappingExpr left, MappingExpr right, int startIndex, int endIndex) {
        super(source, Token.ELVIS, right.isConstant);
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
        if (obj == null || obj instanceof MappingValue) {
            return right.render(scope, args);
        }
        return obj;
    }
}
