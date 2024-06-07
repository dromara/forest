package com.dtflys.forest.exceptions;

import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class MappingExceptionUtil {

    public static String errorMessage(String message, Class<? extends Annotation> annotationType, String attributeName, ForestMethod forestMethod, String source, int startIndex, int endIndex) {
        StringBuilder builder = new StringBuilder();
        builder.append(message);
        if (StringUtils.isNotBlank(source)) {
            builder.append("\n\n\t[From Template]\n\n\t");
            if (forestMethod != null) {
                Method method = forestMethod.getMethod();
                String typeName = method.getDeclaringClass().getTypeName();
                String methodName = method.getName();
                Class<?>[] paramTypes = method.getParameterTypes();
                builder.append("method: ")
                        .append(typeName)
                        .append('.')
                        .append(methodName)
                        .append('(');
                for (int i = 0; i < paramTypes.length; i++) {
                    Class<?> pType = paramTypes[i];
                    builder.append(pType.getName());
                    if (pType.isArray()) {
                        builder.append("[]");
                    }
                    if (i < paramTypes.length - 1) {
                        builder.append(", ");
                    }
                }
                builder.append(")\n\t");
            }
            if (annotationType != null) {
                String annTypeName = annotationType.getSimpleName();
                builder.append("annotation: ")
                        .append(annotationType.getPackage().getName())
                        .append(".@").append(annTypeName)
                        .append("\n\t");
            }
            if (attributeName != null) {
                StringBuilder attrBuilder = new StringBuilder();
                attrBuilder.append("attribute: ")
                        .append(attributeName)
                        .append(" = ")
                        .append("\"");
                int spaceCount = attrBuilder.toString().length() + 1;
                attrBuilder.append(source)
                        .append("\"\n");
                if (startIndex != -1 && endIndex != -1) {
                    attrBuilder.append(errorLine(spaceCount, startIndex, endIndex));
                }
                builder.append(attrBuilder);
            } else {
                builder.append("template: ");
                builder.append(source);
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    private static String errorLine(int spaceCount, int startIndex, int endIndex) {
        char errChar = '^';
        StringBuilder builder = new StringBuilder();
        builder.append("\t");
        for (int i = 0; i < spaceCount; i++) {
            builder.append(' ');
        }
        for (int i = 0; i < startIndex; i++) {
            builder.append(' ');
        }
        for (int i = startIndex; i < endIndex; i++) {
            builder.append(errChar);
        }
        builder.append("\n");
        return builder.toString();
    }


}
