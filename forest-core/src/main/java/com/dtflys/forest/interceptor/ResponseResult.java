package com.dtflys.forest.interceptor;

import com.dtflys.forest.exceptions.ForestRuntimeException;

public interface ResponseResult {

    ResponseSuccess RESPONSE_RESULT_SUCCESS = new ResponseSuccess(true);

    ResponseError RESPONSE_RESULT_ERROR = new ResponseError(null);

    ResponseProceed RESPONSE_RESULT_PROCEED = new ResponseProceed();

    ResponseResultStatus getStatus();

    static ResponseResult proceed() {
        return RESPONSE_RESULT_PROCEED;
    }

    static ResponseResult success() {
        return RESPONSE_RESULT_SUCCESS;
    }

    static ResponseResult success(Object data) {
        return new ResponseSuccess(data);
    }

    static ResponseResult error() {
        return RESPONSE_RESULT_ERROR;
    }

    static ResponseResult error(Throwable e) {
        return new ResponseError(e);
    }

    static ResponseResult error(String msg) {
        return new ResponseError(new ForestRuntimeException(msg));
    }

}
