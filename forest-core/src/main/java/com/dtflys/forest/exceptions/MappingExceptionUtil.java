package com.dtflys.forest.exceptions;

import com.dtflys.forest.ForestGenericClient;
import com.dtflys.forest.mapping.ForestExpressionException;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.ANSIUtil;
import com.dtflys.forest.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class MappingExceptionUtil {

    private static String sourceType(MappingTemplate template) {
        switch (template.getType()) {
            case MappingTemplate.TEMPLATE:
            case MappingTemplate.METHOD_TEMPLATE:
                return "Template";
            case MappingTemplate.URL:
            case MappingTemplate.METHOD_URL:
                return "URL";
            case MappingTemplate.GLOBAL_VARIABLE:
                return "Global Variable";
            case MappingTemplate.METHOD_VARIABLE:
                return "Method Variable";
            case MappingTemplate.REQUEST_VARIABLE:
                return "Request Variable";
            default:
                return "Template";
        }
    }

    private static String attributeType(MappingTemplate template) {
        switch (template.getType()) {
            case MappingTemplate.GLOBAL_VARIABLE:
            case MappingTemplate.METHOD_VARIABLE:
            case MappingTemplate.REQUEST_VARIABLE:
                return "variable";
            default:
                return "attribute";
        }
    }

    private static boolean isVariable(MappingTemplate template) {
        switch (template.getType()) {
            case MappingTemplate.GLOBAL_VARIABLE:
            case MappingTemplate.METHOD_VARIABLE:
            case MappingTemplate.REQUEST_VARIABLE:
                return true;
            default:
                return false;
        }

    }

    public static String errorMessage(String message, Class<? extends Annotation> annotationType, String attributeName, ForestMethod forestMethod, MappingTemplate template, int startIndex, int endIndex, Throwable cause) {
        StringBuilder builder = new StringBuilder();
        builder.append(message);

        if (cause != null) {
            builder.append(". Caused by: ").append(cause.getMessage());
        }

        final String source = template.getSource();

        if (StringUtils.isNotBlank(source)) {
            builder.append("\n\n\t").append(ANSIUtil.colorPurple("[From " + sourceType(template) + "]")).append("\n\n\t");
            if (forestMethod != null && ForestGenericClient.class != forestMethod.getMethod().getDeclaringClass()) {
                Method method = forestMethod.getMethod();
                String typeName = method.getDeclaringClass().getTypeName();
                String methodName = method.getName();
                Class<?>[] paramTypes = method.getParameterTypes();
                builder.append(ANSIUtil.colorPurple("method:"))
                        .append(' ')
                        .append(ANSIUtil.COLOR_BLUE)
                        .append(typeName)
                        .append('#')
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
                builder.append(")")
                        .append(ANSIUtil.COLOR_END)
                        .append("\n\t");
            }
            if (annotationType != null) {
                String annTypeName = annotationType.getSimpleName();
                builder.append(ANSIUtil.colorPurple("annotation:"))
                        .append(' ')
                        .append(ANSIUtil.COLOR_BLUE)
                        .append(annotationType.getPackage().getName())
                        .append('.')
                        .append(ANSIUtil.COLOR_END)
                        .append(ANSIUtil.colorYellow("@" + annTypeName))
                        .append("\n\t");
            }
            if (attributeName != null) {
                StringBuilder attrBuilder = new StringBuilder();
                attrBuilder.append(ANSIUtil.colorPurple(attributeType(template) + ":"))
                        .append(' ')
                        .append(ANSIUtil.COLOR_BLUE)
                        .append(attributeName)
                        .append(isVariable(template) ? " -> " : " = ")
                        .append(ANSIUtil.COLOR_END);
                int spaceCount = ("attribute: " + attributeName + " = \"").length();
                attrBuilder.append(ANSIUtil.colorGreen("\"" + source + "\"")).append("\n");
                if (startIndex != -1 && endIndex != -1) {
                    attrBuilder.append(errorLine(message, spaceCount, startIndex, endIndex));
                }
                builder.append(attrBuilder);
            } else {
                builder.append(ANSIUtil.colorPurple("template:")).append(' ');
                builder.append(ANSIUtil.colorGreen(source));
                int spaceCount = "template: ".length();
                builder.append("\n");
                if (startIndex != -1 && endIndex != -1) {
                    builder.append(errorLine(message, spaceCount, startIndex, endIndex));
                }
            }

        }


        return builder.toString();
    }

    private static String errorLine(String message, int spaceCount, int startIndex, int endIndex) {
        char errChar = '^';
        StringBuilder builder = new StringBuilder();
        StringBuilder spaceBuilder = new StringBuilder();
        for (int i = 0; i < spaceCount; i++) {
            spaceBuilder.append(' ');
        }
        for (int i = 0; i < startIndex - 1; i++) {
            spaceBuilder.append(' ');
        }
        if (startIndex == endIndex) {
            for (int i = startIndex - 1; i < endIndex + 1; i++) {
                builder.append(errChar);
            }
        } else {
            spaceBuilder.append(' ');
            for (int i = startIndex; i < endIndex; i++) {
                builder.append(errChar);
            }            
        }

        builder.append(' ');
        builder.append(message);
        return "\t" + spaceBuilder + ANSIUtil.colorRed(builder.toString()) + "\n";
    }


}
