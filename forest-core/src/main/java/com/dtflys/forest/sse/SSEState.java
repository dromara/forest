package com.dtflys.forest.sse;

/**
 * SSE 监听状态
 * 
 * @since 1.6.2
 */
public enum SSEState {

    /**
     * 初始化状态
     */
    INITIALIZED,

    /**
     * 请求发送状态
     */
    REQUESTING,

    /**
     * 监听状态
     */
    LISTENING,

    /**
     * 发送关闭消息
     */
    CLOSING,

    /**
     * 已关闭状态
     */
    CLOSED,
}
