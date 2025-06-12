package com.dtflys.forest.exceptions;

import com.dtflys.forest.mapping.ForestExpressionException;
import com.dtflys.forest.mapping.MappingExpr;
import com.dtflys.forest.utils.StringUtils;

import java.lang.annotation.Annotation;

public class ForestExpressionNullException extends ForestExpressionException {

    private final MappingExpr expr;


    public ForestExpressionNullException(Class<? extends Annotation> annotationType, String attributeName, String source, String variableName, MappingExpr expr, Throwable cause) {
        super("Null pointer error: " + variableName + " is null", annotationType, attributeName, source, expr, cause);
        this.expr = expr;
    }

    private static String message(final String variableName) {
        if (StringUtils.isEmpty(variableName)) {
            return "Null pointer error";
        }
        return "Null pointer error: " + variableName + " is null";
    }

    public MappingExpr getExpr() {
        return expr;
    }
}
