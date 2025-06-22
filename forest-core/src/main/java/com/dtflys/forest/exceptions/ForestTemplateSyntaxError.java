package com.dtflys.forest.exceptions;

import com.dtflys.forest.mapping.ForestExpressionException;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.reflection.ForestMethod;

import java.lang.annotation.Annotation;

public class ForestTemplateSyntaxError extends ForestExpressionException {

    public ForestTemplateSyntaxError(String message, Class<? extends Annotation> annotationType, String attributeName, ForestMethod method, MappingTemplate template, int startIndex, int endIndex) {
        super(message, annotationType, attributeName, method, template, startIndex, endIndex);
    }
}
