package com.dtflys.forest.exceptions;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.ForestVariableValue;
import com.dtflys.forest.utils.NameUtils;
import com.dtflys.forest.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Forest变量未定义异常
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since v1.5.0
 */
public class ForestVariableUndefinedException extends ForestRuntimeException {


    private final VariableScope variableScope;

    private final String variableName;

    private final String source;

    public ForestVariableUndefinedException(String variableName) {
        this(null, null, null, variableName, null);
    }

    public ForestVariableUndefinedException(String variableName, String source) {
        this(null, null, null, variableName, source);
    }

    public ForestVariableUndefinedException(String attributeName, VariableScope variableScope, String variableName) {
        this(null, attributeName, variableScope, variableName, null);
    }

    public ForestVariableUndefinedException(Class<? extends Annotation> annotationType, String attributeName, VariableScope variableScope, String variableName) {
        this(annotationType, attributeName, variableScope, variableName, null);
    }

    public ForestVariableUndefinedException(Class<? extends Annotation> annotationType, String attributeName, VariableScope variableScope, String variableName, String source) {
        super(getErrorMessage(annotationType, attributeName, variableScope, variableName, source));
        this.variableScope = variableScope;
        this.variableName = variableName;
        this.source = source;
    }

    public VariableScope getVariableScope() {
        return variableScope;
    }

    private static String getErrorMessage(Class<? extends Annotation> annotationType, String attributeName, VariableScope variableScope, String variableName, String source) {
        StringBuilder builder = new StringBuilder();
        builder.append("[Forest] Cannot resolve variable '");
        builder.append(variableName);
        builder.append("'");
        if (StringUtils.isNotBlank(source)) {
            builder.append("\n\n\t[From Template]\n\t");
            if (variableScope != null) {
                ForestMethod forestMethod = variableScope.getForestMethod();
                if (forestMethod != null) {
                    Method method = forestMethod.getMethod();
                    final String methodName = NameUtils.methodAbsoluteName(method);
                    builder.append(methodName).append("\n\t");
                }
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
