package com.dtflys.forest.mapping;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.reflection.ForestMethod;

public class MappingOptionalDot extends MappingDot {

    protected MappingOptionalDot(ForestMethod forestMethod, MappingExpr left, MappingIdentity right, int startIndex, int endIndex) {
        super(forestMethod, Token.OPTIONAL_DOT, left, right, startIndex, endIndex);
    }

    @Override
    public Object render(VariableScope scope, Object[] args) {
        Object obj = left.render(scope, args);
        if (obj == null || obj == MappingEmpty.EMPTY) {
            return MappingEmpty.EMPTY;
        }
        return renderRight(obj, scope, args);
    }
}
