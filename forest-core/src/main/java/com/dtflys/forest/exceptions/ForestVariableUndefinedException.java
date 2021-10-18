package com.dtflys.forest.exceptions;

import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.StringUtils;
import com.sun.org.apache.xml.internal.res.XMLErrorResources_tr;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Forest变量未定义异常
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since v1.5.0
 */
public class ForestVariableUndefinedException extends ForestRuntimeException {

    private final Class<? extends Annotation> annotationType;

    private final String attributeName;

    private final ForestMethod method;

    private final String variableName;

    private final String source;

    public ForestVariableUndefinedException(String variableName) {
        this(null, null, null, variableName, null);
    }

    public ForestVariableUndefinedException(String variableName, String source) {
        this(null, null, null, variableName, source);
    }


    public ForestVariableUndefinedException(String attributeName, ForestMethod method, String variableName) {
        this(null, attributeName, method, variableName, null);
    }


    public ForestVariableUndefinedException(Class<? extends Annotation> annotationType, String attributeName, ForestMethod method, String variableName) {
        this(annotationType, attributeName, method, variableName, null);
    }


    public ForestVariableUndefinedException(Class<? extends Annotation> annotationType, String attributeName, ForestMethod method, String variableName, String source) {
        super(getErrorMessage(annotationType, attributeName, method, variableName, source));
        this.annotationType = annotationType;
        this.attributeName = attributeName;
        this.method = method;
        this.variableName = variableName;
        this.source = source;
    }

    private static String getErrorMessage(Class<? extends Annotation> annotationType, String attributeName, ForestMethod forestMethod, String variableName, String source) {
        StringBuilder builder = new StringBuilder();
        builder.append("[Forest] Cannot resolve variable '");
        builder.append(variableName);
        builder.append("'");
        if (StringUtils.isNotBlank(source)) {
            builder.append("\n\n\t[From Template]\n\t");
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
                builder.append("attribute: ")
                        .append(attributeName)
                        .append(" = ")
                        .append("\"")
                        .append(source)
                        .append("\"\n");
            } else {
                builder.append("template: ");
                builder.append(source);
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public String getVariableName() {
        return variableName;
    }

    public String getSource() {
        return source;
    }
}
