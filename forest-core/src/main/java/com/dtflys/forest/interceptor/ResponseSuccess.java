package com.dtflys.forest.interceptor;

import java.util.Optional;

public class ResponseSuccess implements ResponseResult {

    private final Optional<?> result;

    private final boolean useRequestResult;

    public ResponseSuccess(Object result) {
        this.result = Optional.ofNullable(result);
        this.useRequestResult = false;
    }

    public ResponseSuccess(boolean useRequestResult) {
        this.result = null;
        this.useRequestResult = useRequestResult;
    }

    @Override
    public ResponseResultStatus getStatus() {
        return ResponseResultStatus.SUCCESS;
    }

    public Optional<?> getResult() {
        return result;
    }

    public boolean isUseRequestResult() {
        return useRequestResult;
    }
}
