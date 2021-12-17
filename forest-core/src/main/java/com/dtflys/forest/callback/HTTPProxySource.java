package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;

/**
 * 正向代理信息动态来源接口
 * <p>
 * 本质是一个回调函数: 在创建请求的正向代理时被调用
 * <p>用于动态构建请求的正向代理部分
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-12-17
 **/
public interface HTTPProxySource {


    /**
     * 获取正向代理信息
     * <p>本质是调用回调函数: 在创建请求的正向代理时被调用
     * <p>用于动态构建请求的正向代理部分
     *
     * @param req Forest请求对象
     * @return 正向代理信息, {@link ForestProxy}对象实例
     */
    ForestProxy getProxy(ForestRequest req);
}
