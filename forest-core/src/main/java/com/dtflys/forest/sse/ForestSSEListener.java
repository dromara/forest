package com.dtflys.forest.sse;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestSSE;

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
     * 开始对 SSE 事件流进行监听
     * 
     * @return ForestSSEListener 或其子类对象
     * @param <R> 自身类型
     * @since 1.6.0
     */
    <R extends T> R listen();

    /**
     * 开始对 SSE 事件流进行异步监听
     *
     * @return ForestSSEListener 或其子类对象
     * @param <R> 自身类型
     * @since 1.6.0
     */
    <R extends T> R asyncListen();

    /**
     * 开始对 SSE 事件流进行异步监听
     *
     * @param linesMode 行模式
     * @return ForestSSEListener 或其子类对象
     * @param <R> 自身类型
     * @since 1.6.4
     */
    <R extends T> R asyncListen(SSELinesMode linesMode);


    /**
     * 开始对 SSE 事件流在线程池中进行异步监听
     *
     * @param pool 线程池
     * @return ForestSSEListener 或其子类对象
     * @param <R> 自身类型
     * @since 1.6.0
     */
    <R extends T> R asyncListen(ExecutorService pool);


    /**
     * 开始对 SSE 事件流在线程池中进行异步监听
     * 
     * @param linesMode 行模式
     * @param pool 线程池
     * @return ForestSSEListener 或其子类对象
     * @param <R> 自身类型
     * @since 1.6.4
     */
    <R extends T> R asyncListen(SSELinesMode linesMode, ExecutorService pool);

    /**
     * 阻塞当前线程，直到异步监听结束为止
     * 
     * @return ForestSSEListener 或其子类对象
     * @param <R> 自身类型
     * @since 1.6.2
     */
    <R extends ForestSSE> R await();

    /**
     * 关闭对 SSE 事件流的监听
     * 
     * @return
     * @param <R>
     */
    <R extends T> R close();

}
