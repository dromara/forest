package com.dtflys.forest.exceptions;

public class ForestConditionException extends ForestRuntimeException {
    public ForestConditionException() {
        super("Can not invoke elseThen(), elseIfThen() method, because ifThen() has not been called before");
    }
}
