package com.dtflys.forest.logging;

import com.dtflys.forest.config.ForestConfiguration;

/**
 * 请求日志配置信息
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA5
 */
public class LogConfiguration {

    /**
     * 是否允许打印请求/响应日志
     */
    private volatile boolean logEnabled;

    /**
     * 是否允许打印请求日志
     */
    private volatile boolean logRequest;

    /**
     * 是否允许打印响应状态日志
     */
    private volatile boolean logResponseStatus;

    /**
     * 是否允许打印响应头日志
     */
    private volatile boolean logResponseHeaders;

    /**
     * 是否允许打印响应内容日志
     */
    private volatile boolean logResponseContent;

    /**
     * 日志处理器
     */
    private volatile ForestLogHandler logHandler;

    public LogConfiguration() {
    }

    public LogConfiguration(ForestConfiguration configuration) {
        if (configuration != null) {
            logEnabled = configuration.isLogEnabled();
            logRequest = configuration.isLogRequest();
            logResponseStatus = configuration.isLogResponseStatus();
            logResponseHeaders = configuration.isLogResponseHeaders();
            logResponseContent = configuration.isLogResponseContent();
            logHandler = configuration.getLogHandler();
        }
    }


    /**
     * 是否允许打印请求/响应日志
     * @return 允许为 {@code true}, 否则为 {@code false}
     */
    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

    /**
     * 是否允许打印请求日志
     * @return 允许为 {@code true}, 否则为 {@code false}
     */
    public boolean isLogRequest() {
        return logRequest;
    }

    public void setLogRequest(boolean logRequest) {
        this.logRequest = logRequest;
    }

    /**
     * 是否允许打印响应日志
     *
     * @return 允许为 {@code true}, 否则为 {@code false}
     */
    public boolean isLogResponseStatus() {
        return logResponseStatus;
    }

    public void setLogResponseStatus(boolean logResponseStatus) {
        this.logResponseStatus = logResponseStatus;
    }

    /**
     * 是否允许打印响应头日志
     *
     * @return 允许为 {@code true}, 否则为 {@code false}
     */
    public boolean isLogResponseHeaders() {
        return logResponseHeaders;
    }

    public void setLogResponseHeaders(boolean logResponseHeaders) {
        this.logResponseHeaders = logResponseHeaders;
    }

    /**
     * 是否允许打印响应日志
     *
     * @return 允许为 {@code true}, 否则为 {@code false}
     */
    public boolean isLogResponseContent() {
        return logResponseContent;
    }

    public void setLogResponseContent(boolean logResponseContent) {
        this.logResponseContent = logResponseContent;
    }

    /**
     * 获取日志处理器
     *
     * @return 日志处理器接口实例
     */
    public ForestLogHandler getLogHandler() {
        return logHandler;
    }

    /**
     * 设置日志处理器
     *
     * @param logHandler 日志处理器接口实例
     */
    public void setLogHandler(ForestLogHandler logHandler) {
        this.logHandler = logHandler;
    }
}
