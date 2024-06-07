package com.dtflys.forest.exceptions;

import com.dtflys.forest.reflection.ForestMethod;

import java.lang.annotation.Annotation;

public class MappingExpressionException extends RuntimeException {

    protected final Class<? extends Annotation> annotationType;

    protected final String attributeName;

    protected final ForestMethod method;


    protected final String source;

    protected final int startIndex;

    protected final int endIndex;

    public MappingExpressionException(String message, Class<? extends Annotation> annotationType, String attributeName, ForestMethod method, String source, int startIndex, int endIndex) {
        this(message, annotationType, attributeName, method, source, startIndex, endIndex, null);
    }

    public MappingExpressionException(String message, Class<? extends Annotation> annotationType, String attributeName, ForestMethod method, String source, int startIndex, int endIndex, Throwable cause) {
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
