package com.dtflys.forest.interceptor;

import com.dtflys.forest.exceptions.ForestRuntimeException;

/**
 * Forest 响应结果
 * <p> Forest 响应结果并不直接包含 Response 的数据实体，而是代表一种状态: 成功、失败、或继续
 * <p> 可供选择的结果状态:
 * <pre>
 *   - success (成功): 代表请求响应成功，并直接跳转到 OnSuccess 环节，不再进行其他处理。
 *   - error (错误)：代表请求响应失败，并直接跳转到 OnError 进行错误处理，也不再进行其他处理。
 *   - proceed (继续)：代表可以继续往下正常处理。
 * </pre>
 *
 * @since 1.6.4
 */
public interface ResponseResult {

    ResponseSuccess RESPONSE_RESULT_SUCCESS = new ResponseSuccess(true);

    ResponseError RESPONSE_RESULT_ERROR = new ResponseError(null);

    ResponseProceed RESPONSE_RESULT_PROCEED = new ResponseProceed();

    /**
     * 获取响应结果状态
     *
     * @return 响应结果状态枚举对象
     */
    ResponseResultStatus getStatus();

    /**
     * 继续: 代表可以继续往下正常执行后续生命周期的环节
     *
     * @return 响应结果实现类 {@link ResponseProceed} 的实例对象
     */
    static ResponseResult proceed() {
        return RESPONSE_RESULT_PROCEED;
    }

    /**
     * 成功: 代表请求响应成功，并直接跳转到 OnSuccess 环节，不再进行其他处理
     *
     * @return 响应结果实现类 {@link ResponseSuccess} 的实例对象
     */
    static ResponseResult success() {
        return RESPONSE_RESULT_SUCCESS;
    }

    /**
     * 成功: 代表请求响应成功，并直接跳转到 OnSuccess 环节，不再进行其他处理
     * <p>并可以传入可自定义的最终结果数据对象
     *
     * @param data 结果数据对象
     * @return 带有结果对象的，响应结果实现类 {@link ResponseSuccess} 的实例对象
     */
    static ResponseResult success(Object data) {
        return new ResponseSuccess(data);
    }

    /**
     * 错误: 代表请求响应失败，并直接跳转到 OnError 进行错误处理，也不再进行其他处理
     *
     * @return 响应结果实现类 {@link ResponseError} 的实例对象
     */
    static ResponseResult error() {
        return RESPONSE_RESULT_ERROR;
    }

    /**
     * 错误: 代表请求响应失败，并直接跳转到 OnError 进行错误处理，也不再进行其他处理
     * <p>并可以传入可自定义的异常对象
     *
     * @param e 异常对象
     * @return 带有异常对象的，响应结果实现类 {@link ResponseError} 的实例对象
     */
    static ResponseResult error(Throwable e) {
        return new ResponseError(e);
    }

    /**
     * 错误: 代表请求响应失败，并直接跳转到 OnError 进行错误处理，也不再进行其他处理
     * <p>并可以传入可自定义的错误消息
     *
     * @param msg 错误消息
     * @return 带有错误消息的，响应结果实现类 {@link ResponseError} 的实例对象
     */
    static ResponseResult error(String msg) {
        return new ResponseError(new ForestRuntimeException(msg));
    }

}
