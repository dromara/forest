package com.dtflys.forest.interceptor;

import com.dtflys.forest.exceptions.ForestRuntimeException;

public interface ResponseResult {

    ResponseSuccess SUCCESS = new ResponseSuccess(true);

    ResponseError ERROR = new ResponseError(null);

    ResponseProceed PROCEED = new ResponseProceed();

    ResponseResultStatus getStatus();

    static ResponseResult proceed() {
        return PROCEED;
    }

    static ResponseResult success() {
        return SUCCESS;
    }

    static ResponseResult success(Object data) {
        return new ResponseSuccess(data);
    }

    static ResponseResult error() {
        return ERROR;
    }

    static ResponseResult error(Throwable e) {
        return new ResponseError(e);
    }

    static ResponseResult error(String msg) {
        return new ResponseError(new ForestRuntimeException(msg));
    }

}
