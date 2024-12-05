package com.dtflys.forest.exceptions;

import com.dtflys.forest.reflection.ForestMethod;

import java.lang.annotation.Annotation;

/**
 * Forest变量未定义异常
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0
 */
public class ForestIndexReferenceException extends MappingExpressionException {


    private final int index;
    
    private final int argumentsLength;


    public ForestIndexReferenceException(int index, int argumentsLength) {
        this(null, null, null, index, argumentsLength, null, -1, -1);
    }


    public ForestIndexReferenceException(int index, int argumentsLength, int startIndex, int endIndex) {
        this(null, null, null, index, argumentsLength, null, startIndex, endIndex);
    }

        public ForestIndexReferenceException(int index, int argumentsLength, String source) {
        this(null, null, null, index, argumentsLength, source, -1, -1);
    }


    public ForestIndexReferenceException(int index, int argumentsLength, String source, int startIndex, int endIndex) {
        this(null, null, null, index, argumentsLength, source, startIndex, endIndex);
    }

    public ForestIndexReferenceException(String attributeName, ForestMethod method, int index, int argumentsLength) {
        this(null, attributeName, method, index, argumentsLength, null, -1, -1);
    }


    public ForestIndexReferenceException(String attributeName, ForestMethod method, int index, int argumentsLength, int startIndex, Integer endIndex) {
        this(null, attributeName, method, index, argumentsLength, null, startIndex, endIndex);
    }


    public ForestIndexReferenceException(Class<? extends Annotation> annotationType, String attributeName, ForestMethod method, int index, int argumentsLength, int startIndex, int endIndex) {
        this(annotationType, attributeName, method, index, argumentsLength, null, startIndex, endIndex);
    }


    public ForestIndexReferenceException(Class<? extends Annotation> annotationType, String attributeName, ForestMethod method, int index, int argumentsLength, String source, int startIndex, int endIndex) {
        super("Index " + index + " out of bounds for arguments length " + argumentsLength, annotationType, attributeName, method, source, startIndex, endIndex);
        this.index = index;
        this.argumentsLength = argumentsLength;
    }


    public int getIndex() {
        return index;
    }

    public int getArgumentsLength() {
        return argumentsLength;
    }
}
