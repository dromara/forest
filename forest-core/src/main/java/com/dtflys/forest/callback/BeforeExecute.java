package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.ForestJoinpoint;

/**
 * 回调函数: 请求发送前调用
 *
 * @author gongjun
 * @since 2.0.0-BETA
 */
@FunctionalInterface
public interface BeforeExecute {

    /**
     * 请求发送前调用该方法
     *
     * @param request Forest请求对象
     * @return {@link ForestJoinpoint}: Forest 拦截器插入点
     */
    ForestJoinpoint beforeExecute(ForestRequest request);


    /**
     * 继续执行
     *
     * @return {@link ForestJoinpoint}: Forest 拦截器插入点
     * @since 2.0.0-BETA
     */
    default ForestJoinpoint proceed() {
        return ForestJoinpoint.PROCEED;
    }

    /**
     * 中断请求
     * <br>后续不会发送请求，而是直退出请求
     *
     * @return {@link ForestJoinpoint}: Forest 拦截器插入点
     * @since 2.0.0-BETA
     */
    default ForestJoinpoint cutoff() {
        return ForestJoinpoint.CUTOFF;
    }


    /**
     * 中断请求，并直接返回结果
     * <br>后续不会发送请求，而是直退出请求，并返回参数输入的结果
     *
     * @param result 要返回的结果
     * @return {@link ForestJoinpoint}: Forest 拦截器插入点
     * @since 2.0.0-BETA
     */
    default ForestJoinpoint cutoff(Object result) {
        return new ForestJoinpoint(ForestJoinpoint.State.CUTOFF, result);
    }

}
