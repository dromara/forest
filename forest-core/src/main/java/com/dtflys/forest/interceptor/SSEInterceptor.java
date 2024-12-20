package com.dtflys.forest.interceptor;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
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
     * @param eventSource SSE 事件来源
     * @since 1.6.1
     */
    default void onSSEOpen(EventSource eventSource) {
    }

    /**
     * 监听关闭回调函数：在结束 SSE 数据流监听的时候调用
     *
     * @param request Forest 请求对象
     * @param response Forest 响应对象
     * @since 1.6.1
     */
    default void onSSEClose(ForestRequest request, ForestResponse response) {
    }
    
}
