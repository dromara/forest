package org.dromara.forest.http;

/**
 * Forest 异步请求模式
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.27
 */
public enum ForestAsyncMode {

    /**
     * 平台模式 - 默认模式，基于 JVM 自带的线程池
     *
     * @since 1.5.27
     */
    PLATFORM,

    /**
     * Kotlin协程 - 基于 Kotlin 语言的协程进行并发调用
     *
     * @since 1.5.27
     */
    KOTLIN_COROUTINE,
}
