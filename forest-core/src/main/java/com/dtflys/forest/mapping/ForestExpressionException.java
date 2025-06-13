package com.dtflys.forest.mapping;

import com.dtflys.forest.exceptions.MappingExceptionUtil;
import com.dtflys.forest.reflection.ForestMethod;

import java.lang.annotation.Annotation;

public class ForestExpressionException extends RuntimeException {

    protected Class<? extends Annotation> annotationType;

    protected String attributeName;

    protected ForestMethod method;

    protected MappingTemplate template;

    protected int startIndex;

    protected int endIndex;

    protected Throwable expressionCause;

    public ForestExpressionException(String message, Class<? extends Annotation> annotationType, String attributeName, ForestMethod method, MappingTemplate template, int startIndex, int endIndex) {
        this(message, annotationType, attributeName, method, template, startIndex, endIndex, null);
    }

    public ForestExpressionException(String message, Class<? extends Annotation> annotationType, String attributeName, MappingTemplate template, MappingExpr expr, Throwable cause) {
        this(message, annotationType, attributeName, expr.getForestMethod(), template, expr.getStartIndex(), expr.getEndIndex(), cause instanceof ForestExpressionException ? null : cause);
        if (cause instanceof ForestExpressionException) {
            this.expressionCause = cause;
        }
    }


    public ForestExpressionException(String message, Class<? extends Annotation> annotationType, String attributeName, ForestMethod method, MappingTemplate template, int startIndex, int endIndex, Throwable cause) {
        super(MappingExceptionUtil.errorMessage(message, annotationType, attributeName, method, template, startIndex, endIndex, cause), cause instanceof ForestExpressionException ? null : cause);
        this.annotationType = annotationType;
        this.attributeName = attributeName;
        this.method = method;
        this.template = template;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        if (cause instanceof ForestExpressionException) {
            this.expressionCause = cause;
        }
    }

    public Class<? extends Annotation> getAnnotationType() {
        return annotationType;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public ForestMethod getMethod() {
        return method;
    }

    public MappingTemplate getTemplate() {
        return template;
    }

    public String getSource() {
        return template.getSource();
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public Throwable getExpressionCause() {
        if (expressionCause != null) {
            return expressionCause;
        }
        return getCause();
    }
}
