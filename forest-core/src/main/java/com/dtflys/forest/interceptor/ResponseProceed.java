package com.dtflys.forest.interceptor;

public class ResponseProceed implements ResponseResult {
    @Override
    public ResponseResultStatus getStatus() {
        return ResponseResultStatus.PROCEED;
    }

}
