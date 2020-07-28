package com.dtflys.forest.exceptions;

public class ForestNoFileNameException extends ForestRuntimeException {

    public ForestNoFileNameException(Class type) {
        super("[Forest] \"" + type.getName() + "\" parameters width @DataFile annotation must define a fileName");
    }
}
