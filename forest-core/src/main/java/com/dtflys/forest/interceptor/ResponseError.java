package com.dtflys.forest.interceptor;

public class ResponseError implements ResponseResult {

    private final Throwable exception;

    public ResponseError(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public ResponseResultStatus getStatus() {
        return ResponseResultStatus.ERROR;
    }

    public Throwable getException() {
        return exception;
    }
}
