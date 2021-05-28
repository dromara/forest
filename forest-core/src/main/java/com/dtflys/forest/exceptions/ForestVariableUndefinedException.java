package com.dtflys.forest.exceptions;

import com.dtflys.forest.utils.StringUtils;

/**
 * Forest变量未定义异常
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since v1.5.0
 */
public class ForestVariableUndefinedException extends ForestRuntimeException {

    private final String variableName;

    private final String source;

    public ForestVariableUndefinedException(String variableName) {
        super(getErrorMessage(variableName, null));
        this.variableName = variableName;
        this.source = null;
    }


    public ForestVariableUndefinedException(String variableName, String source) {
        super(getErrorMessage(variableName, source));
        this.variableName = variableName;
        this.source = source;
    }

    private static String getErrorMessage(String variableName, String source) {
        StringBuilder builder = new StringBuilder();
        builder.append("[Forest] Cannot resolve variable '");
        builder.append(variableName);
        builder.append("'");
        if (StringUtils.isNotBlank(source)) {
            builder.append("\n\n\t[Form Template] ");
            builder.append(source);
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
