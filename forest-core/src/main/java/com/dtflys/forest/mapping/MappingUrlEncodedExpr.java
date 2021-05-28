package com.dtflys.forest.mapping;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.URLUtils;

import java.io.UnsupportedEncodingException;

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
    public Object render(Object[] args) {
        Object ret = expr.render(args);
        if (ret == null) {
            return null;
        }
        String str = String.valueOf(ret);
        try {
            return URLUtils.forceEncode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ForestRuntimeException(e);
        }
    }
}
