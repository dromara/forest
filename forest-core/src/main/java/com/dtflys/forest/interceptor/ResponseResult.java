package com.dtflys.forest.interceptor;

public interface ResponseResult {

    ResponseSuccess SUCCESS = new ResponseSuccess(true);

    ResponseError ERROR = new ResponseError(null);

    ResponseProceed PROCEED = new ResponseProceed();

    ResponseResultStatus getStatus();


}
