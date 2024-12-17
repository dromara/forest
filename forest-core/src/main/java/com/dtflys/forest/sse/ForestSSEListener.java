package com.dtflys.forest.sse;

import com.dtflys.forest.http.ForestRequest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Forest SSE 监听器
 * 
 * @param <T> 自身类型
 * @since 1.6.0
 */
public interface ForestSSEListener<T extends ForestSSEListener<T>> extends SSEStringMessageConsumer {


    /**
     * 获取 Forest 请求对象
     * @return Forest 请求对象
     * @since 1.6.0
     */
    ForestRequest getRequest();

    /**
     * 开始对 SSE 数据流进行监听
     * 
     * @return ForestSSEListener 或其子类对象
     * @param <R> 自身类型
     * @since 1.6.0
     */
    <R extends T> R listen();

    /**
     * 开始对 SSE 数据流进行异步监听
     * 
     * @return ForestSSEListener 或其子类对象
     * @param <R> 自身类型
     * @since 1.6.0
     */
    <R extends T> CompletableFuture<R> asyncListen();

    /**
     * 开始对 SSE 数据流在线程池中进行异步监听
     * 
     * @param pool 线程池
     * @return ForestSSEListener 或其子类对象
     * @param <R> 自身类型
     * @since 1.6.0
     */
    <R extends T> CompletableFuture<R> asyncListen(ExecutorService pool);

}
