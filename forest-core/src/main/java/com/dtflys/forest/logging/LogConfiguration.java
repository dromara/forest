package com.dtflys.forest.logging;

public class LogConfiguration {

    /**
     * 是否允许打印请求/响应日志
     */
    private boolean logEnabled;

    /**
     * 是否允许打印请求日志
     */
    private boolean logRequest;

    /**
     * 是否允许打印响应状态日志
     */
    private boolean logResponseStatus;

    /**
     * 是否允许打印响应内容日志
     */
    private boolean logResponseContent;

    /**
     * 日志处理器
     */
    private LogHandler logHandler;

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
     * @return 允许为 {@code true}, 否则为 {@code false}
     */
    public boolean isLogResponseStatus() {
        return logResponseStatus;
    }

    public void setLogResponseStatus(boolean logResponseStatus) {
        this.logResponseStatus = logResponseStatus;
    }

    /**
     * 是否允许打印响应日志
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
     * @return 日志处理器接口实例
     */
    public LogHandler getLogHandler() {
        return logHandler;
    }

    public void setLogHandler(LogHandler logHandler) {
        this.logHandler = logHandler;
    }
}
