package com.dtflys.forest.exceptions;

import com.dtflys.forest.reflection.ForestMethod;

import java.lang.annotation.Annotation;

/**
 * Forest变量未定义异常
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0
 */
public class ForestVariableUndefinedException extends ForestExpressionException {


    private final String variableName;


    public ForestVariableUndefinedException(String variableName) {
        this(null, null, null, variableName, null, -1, -1);
    }


    public ForestVariableUndefinedException(String variableName, int startIndex, int endIndex) {
        this(null, null, null, variableName, null, startIndex, endIndex);
    }

        public ForestVariableUndefinedException(String variableName, String source) {
        this(null, null, null, variableName, source, -1, -1);
    }


    public ForestVariableUndefinedException(String variableName, String source, int startIndex, int endIndex) {
        this(null, null, null, variableName, source, startIndex, endIndex);
    }

    public ForestVariableUndefinedException(String attributeName, ForestMethod method, String variableName) {
        this(null, attributeName, method, variableName, null, -1, -1);
    }


    public ForestVariableUndefinedException(String attributeName, ForestMethod method, String variableName, int startIndex, Integer endIndex) {
        this(null, attributeName, method, variableName, null, startIndex, endIndex);
    }


    public ForestVariableUndefinedException(Class<? extends Annotation> annotationType, String attributeName, ForestMethod method, String variableName, int startIndex, int endIndex) {
        this(annotationType, attributeName, method, variableName, null, startIndex, endIndex);
    }


    public ForestVariableUndefinedException(Class<? extends Annotation> annotationType, String attributeName, ForestMethod method, String variableName, String source, int startIndex, int endIndex) {
        super("Cannot resolve variable '" + variableName + "'", annotationType, attributeName, method, source, startIndex, endIndex);
        this.variableName = variableName;
    }


    public String getVariableName() {
        return variableName;
    }

}
