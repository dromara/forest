package com.dtflys.forest.exceptions;

public class ForestConvertException extends ForestRuntimeException {

    public ForestConvertException(String converterName, Throwable th) {
        super("[Forest] Converter '" + converterName + "' Error: " + th.getMessage(), th);
    }
}
