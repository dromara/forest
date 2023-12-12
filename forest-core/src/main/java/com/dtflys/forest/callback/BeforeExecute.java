package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.ForestJointPoint;

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
     * @return {@link ForestJointPoint}: Forest 拦截器插入点
     */
    ForestJointPoint beforeExecute(ForestRequest request);


    /**
     * 继续执行
     *
     * @return {@link ForestJointPoint}: Forest 拦截器插入点
     * @since 2.0.0-BETA
     */
    default ForestJointPoint proceed() {
        return ForestJointPoint.PROCEED;
    }

    /**
     * 中断请求
     * <br>后续不会发送请求，而是直退出请求
     *
     * @return {@link ForestJointPoint}: Forest 拦截器插入点
     * @since 2.0.0-BETA
     */
    default ForestJointPoint cutoff() {
        return ForestJointPoint.CUTOFF;
    }


    /**
     * 中断请求，并直接返回结果
     * <br>后续不会发送请求，而是直退出请求，并返回参数输入的结果
     *
     * @param result 要返回的结果
     * @return {@link ForestJointPoint}: Forest 拦截器插入点
     * @since 2.0.0-BETA
     */
    default ForestJointPoint cutoff(Object result) {
        return new ForestJointPoint(ForestJointPoint.State.CUTOFF, result);
    }

}
