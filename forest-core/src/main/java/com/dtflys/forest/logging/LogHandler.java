package com.dtflys.forest.logging;

/**
 * 日志处理器接口
 *
 * @see DefaultLogHandler
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-14 17:31
 */
public interface LogHandler {

    /**
     * 获取Forest日志控制对象
     * @return
     */
    ForestLogger getLogger();

    /**
     * 设置Forest日志控制对象
     * @param logger
     */
    void setLogger(ForestLogger logger);

    /**
     * 打印请求日志
     * @param requestLogMessage 请求日志消息
     */
    void logRequest(RequestLogMessage requestLogMessage);

    /**
     * 打印响应状态日志
     * @param responseLogMessage 响应日志消息
     */
    void logResponseStatus(ResponseLogMessage responseLogMessage);

    /**
     * 打印响应内容日志
     * @param responseLogMessage 响应日志消息
     */
    void logResponseContent(ResponseLogMessage responseLogMessage);

}
