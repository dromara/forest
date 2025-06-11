package com.dtflys.forest.exceptions;

import com.dtflys.forest.mapping.MappingExpr;

import java.lang.annotation.Annotation;

public class ForestExpressionNullException extends ForestExpressionException {

    private final MappingExpr expr;

    public ForestExpressionNullException(MappingExpr expr, Throwable cause) {
        super("Null Pointer Error", null, null, null, expr, cause);
        this.expr = expr;
    }


    public ForestExpressionNullException(Class<? extends Annotation> annotationType, String attributeName, String source, MappingExpr expr, Throwable cause) {
        super("Null Pointer Error", annotationType, attributeName, source, expr, cause);
        this.expr = expr;
    }

    public MappingExpr getExpr() {
        return expr;
    }
}
