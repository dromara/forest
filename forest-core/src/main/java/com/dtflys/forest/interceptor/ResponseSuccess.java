package com.dtflys.forest.interceptor;

public class ResponseSuccess implements ResponseResult {

    private final Object result;

    private final boolean useRequestResult;

    public ResponseSuccess(Object result) {
        this.result = result;
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

    public Object getResult() {
        return result;
    }

    public boolean isUseRequestResult() {
        return useRequestResult;
    }
}
