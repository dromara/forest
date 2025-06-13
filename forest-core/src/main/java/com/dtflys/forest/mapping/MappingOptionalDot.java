package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;

public class MappingOptionalDot extends MappingDot {

    protected MappingOptionalDot(MappingTemplate source, MappingExpr left, MappingIdentity right, int startIndex, int endIndex) {
        super(source, Token.OPTIONAL_DOT, left, right, startIndex, endIndex);
    }

    @Override
    public Object render(VariableScope scope, Object[] args) {
        Object obj = left.render(scope, args);
        if (obj == null || obj == MappingEmpty.OPTIONAL) {
            return MappingEmpty.OPTIONAL;
        }
        return checkDeepReference(renderRight(obj), this, scope, args);
    }
}
