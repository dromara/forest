package com.dtflys.forest.callback;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.ResponseError;
import com.dtflys.forest.interceptor.ResponseResult;
import com.dtflys.forest.interceptor.ResponseSuccess;
import com.dtflys.forest.interceptor.ResponseProceed;


/**
 * 回调函数: 接受到请求响应时调用
 * @since 1.6.0
 */
@FunctionalInterface
public interface OnResponse {

    default ResponseSuccess success() {
        return ResponseResult.RESPONSE_RESULT_SUCCESS;
    }

    default ResponseError error() {
        return ResponseResult.RESPONSE_RESULT_ERROR;
    }

    default ResponseSuccess success(Object data) {
        return new ResponseSuccess(data);
    }

    default ResponseError error(Throwable exception) {
        return new ResponseError(exception);
    }

    default ResponseError error(String message) {
        return new ResponseError(new ForestRuntimeException(message));
    }

    default ResponseProceed proceed() {
        return ResponseResult.RESPONSE_RESULT_PROCEED;
    }

    /**
     * 接受到请求响应时调用该方法
     *
     * @param req Forest请求对象
     * @param res Forest响应对象
     * @return 请求响应结果: {@link ResponseSuccess} 或 {@link ResponseSuccess} 实例
     * @since 1.6.0
     */
    ResponseResult onResponse(ForestRequest req, ForestResponse<?> res);

}
