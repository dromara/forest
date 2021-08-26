package com.dtflys.forest.exceptions;

/**
 * 未包含文件名异常
 *
 * @author gongjun[dt_flys@hotmail.com]
 */
public class ForestNoFileNameException extends ForestRuntimeException {

    private final Class<?> parameterType;

    public ForestNoFileNameException(Class<?> type) {
        super("[Forest] '" + type.getName() + "' parameters width @DataFile annotation must define a fileName");
        this.parameterType = type;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }
}
