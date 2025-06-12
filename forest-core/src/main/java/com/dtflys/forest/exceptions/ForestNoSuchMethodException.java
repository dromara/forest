package com.dtflys.forest.exceptions;

import com.dtflys.forest.mapping.ForestExpressionException;
import com.dtflys.forest.reflection.ForestMethod;

import java.lang.annotation.Annotation;

public class ForestNoSuchMethodException extends ForestExpressionException {


    public ForestNoSuchMethodException(Class<? extends Annotation> annotationType, String attributeName, ForestMethod method, String source, NoSuchMethodException ex, int startIndex, int endIndex) {
        super(ex.getMessage(), annotationType, attributeName, method, source, startIndex, endIndex, ex);
    }

}
