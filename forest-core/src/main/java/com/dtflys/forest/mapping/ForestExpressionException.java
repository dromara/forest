package com.dtflys.forest.mapping;

import com.dtflys.forest.exceptions.MappingExceptionUtil;
import com.dtflys.forest.reflection.ForestMethod;

import java.lang.annotation.Annotation;

public class ForestExpressionException extends RuntimeException {

    protected Class<? extends Annotation> annotationType;

    protected String attributeName;

    protected ForestMethod method;

    protected String source;

    int startIndex;

    int endIndex;

    public ForestExpressionException(String message, Class<? extends Annotation> annotationType, String attributeName, ForestMethod method, String source, int startIndex, int endIndex) {
        this(message, annotationType, attributeName, method, source, startIndex, endIndex, null);
    }

    public ForestExpressionException(String message, Class<? extends Annotation> annotationType, String attributeName, String source, MappingExpr expr, Throwable cause) {
        this(message, annotationType, attributeName, expr.getForestMethod(), source, expr.getStartIndex(), expr.getEndIndex(), cause);
    }


    public ForestExpressionException(String message, Class<? extends Annotation> annotationType, String attributeName, ForestMethod method, String source, int startIndex, int endIndex, Throwable cause) {
        super(MappingExceptionUtil.errorMessage(message, annotationType, attributeName, method, source, startIndex, endIndex), cause);
        this.annotationType = annotationType;
        this.attributeName = attributeName;
        this.method = method;
        this.source = source;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
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

    public String getSource() {
        return source;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
}
