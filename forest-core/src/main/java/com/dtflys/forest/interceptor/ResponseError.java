package com.dtflys.forest.interceptor;

public class ResponseError implements ResponseResult {

    private final Exception exception;

    public ResponseError(Exception exception) {
        this.exception = exception;
    }

    @Override
    public ResponseResultStatus getStatus() {
        return ResponseResultStatus.ERROR;
    }

    public Exception getException() {
        return exception;
    }
}
