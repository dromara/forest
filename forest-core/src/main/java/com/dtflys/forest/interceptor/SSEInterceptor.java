package com.dtflys.forest.interceptor;

import com.dtflys.forest.sse.EventSource;

import java.io.InputStream;

/**
 * Forest SSE 拦截器
 * 
 * @since 1.6.1
 */
public interface SSEInterceptor extends Interceptor<InputStream> {

    /**
     * 监听打开回调函数：在开始 SSE 数据流监听的时候调用
     * 
     * @param event SSE 事件来源
     * @since 1.6.1
     */
    default void onSSEOpen(EventSource event) {
    }

    /**
     * 监听关闭回调函数：在结束 SSE 数据流监听的时候调用
     *
     * @param event SSE 事件来源
     * @since 1.6.1
     */
    default void onSSEClose(EventSource event) {
    }

    /**
     * 消息回调函数：在接收到 SSE 消息后调用
     *
     * @param event 消息源
     * @since 1.6.4
     */
    default void onMessage(EventSource event) {
    }

    /**
     * 消息回调函数：在接收到 SSE 消息后调用
     *
     * @param event 消息源
     * @param name 名称
     * @param value 值
     * @since 1.6.4
     */
    default void onMessage(EventSource event, String name, String value) {
    }
    
}
