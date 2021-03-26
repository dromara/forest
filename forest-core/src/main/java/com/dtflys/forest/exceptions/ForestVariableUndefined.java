package com.dtflys.forest.exceptions;

/**
 * Forest变量未定义异常
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since v1.5.0
 */
public class ForestVariableUndefined extends ForestRuntimeException {

    private String variableName;

    public ForestVariableUndefined(String variableName) {
        super("[Forest] Cannot resolve variable '" + variableName + "'");
        this.variableName = variableName;
    }
}
