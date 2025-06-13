package com.dtflys.forest.exceptions;

import com.dtflys.forest.mapping.ForestExpressionException;
import com.dtflys.forest.mapping.MappingExpr;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.reflection.ForestMethod;

import java.lang.annotation.Annotation;

/**
 * Forest变量引用异常
 *
 * @since 1.7.1
 */
public class ForestReferenceException extends ForestExpressionException {

    public ForestReferenceException(Class<? extends Annotation> annotationType, String attributeName, ForestMethod method, MappingTemplate source, MappingExpr expr, int startIndex, int endIndex, Throwable cause) {
        super("Reference error: " + expr.toTemplateString(), annotationType, attributeName, method, source, startIndex, endIndex, cause);
    }

    public ForestReferenceException(Class<? extends Annotation> annotationType, String attributeName, ForestMethod method, MappingTemplate source, String refTemplate, MappingExpr expr, int startIndex, int endIndex, Throwable cause) {
        super("Reference error: " + expr.toTemplateString() + wrapReferenceTemplate(refTemplate), annotationType, attributeName, method, source, startIndex, endIndex, cause);
    }

    private static String wrapReferenceTemplate(String refTemplate) {
        final StringBuilder builder = new StringBuilder();
        if (refTemplate == null) {
            return "";
        }
        builder.append(" -> \"");
        final int maxChars = 32;
        if (refTemplate.length() < maxChars && refTemplate.indexOf('\n') == -1) {
            builder.append(refTemplate);
        } else {
            final int nextLineIndex = refTemplate.indexOf('\n');
            final int index = nextLineIndex == -1 ? maxChars : Math.min(maxChars, nextLineIndex - 1);
            builder.append(refTemplate, 0, index).append("...");
        }
        builder.append("\"");
        return builder.toString();
    }

}
