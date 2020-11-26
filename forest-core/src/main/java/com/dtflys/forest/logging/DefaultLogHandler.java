package com.dtflys.forest.logging;

import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.StringUtils;

import java.util.Iterator;
import java.util.List;

/**
 * 默认日志处理器
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-14 17:31
 */
public class DefaultLogHandler implements ForestLogHandler {

    private ForestLogger logger = new ForestLogger();

    /**
     * 获取请求头日志内容
     * @param requestLogMessage 请求日志消息
     * @return 请求头日志内容
     */
    protected String requestLoggingHeaders(RequestLogMessage requestLogMessage) {
        StringBuilder builder = new StringBuilder();
        List<LogHeaderMessage> headers = requestLogMessage.getHeaders();
        if (headers == null) {
            return "";
        }
        for (int i = 0; i < headers.size(); i++) {
            LogHeaderMessage headerMessage = headers.get(i);
            String name = headerMessage.getName();
            String value = headerMessage.getValue();
            builder.append("\t\t" + name + ": " + value);
            if (i < headers.size() - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    /**
     * 获取请求体日志内容
     * @param requestLogMessage 请求日志消息
     * @return 请求体字符串
     */
    protected String requestLoggingBody(RequestLogMessage requestLogMessage) {
        LogBodyMessage logBodyMessage = requestLogMessage.getBody();
        if (logBodyMessage == null) {
            return "";
        }
        return logBodyMessage.getBodyString();
    }

    /**
     * 获取请求类型变更历史日志内容
     * @param requestLogMessage 请求日志消息
     * @return 日志内容字符串
     */
    protected String requestTypeChangeHistory(RequestLogMessage requestLogMessage) {
        List<String> typeChangeHistory = requestLogMessage.getTypeChangeHistory();
        if (typeChangeHistory == null || typeChangeHistory.size() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("[Type Change]: ");
        for (Iterator<String> iterator = typeChangeHistory.iterator(); iterator.hasNext(); ) {
            String type = iterator.next();
            builder.append(type).append(" -> ");
        }
        builder.append(requestLogMessage.getType()).append("\n\t");
        return builder.toString();
    }

    /**
     * 请求失败重试信息
     * @param requestLogMessage 请求日志消息，{@link RequestLogMessage}类实例
     * @return 重试信息字符串
     */
    protected String retryContent(RequestLogMessage requestLogMessage) {
        int retryCount = requestLogMessage.getRetryCount();
        if (retryCount > 0) {
            return "[Retry]: " + retryCount + "\n\t";
        }
        return "";
    }

    /**
     * 正向代理信息
     * @param requestLogMessage 请求日志消息，{@link RequestLogMessage}类实例
     * @return 正向代理日志字符串
     */
    protected String proxyContent(RequestLogMessage requestLogMessage) {
        RequestProxyLogMessage proxyLogMessage = requestLogMessage.getProxy();
        if (proxyLogMessage != null) {
            return "[Proxy]: host: " + proxyLogMessage.getHost() + ", port: " + proxyLogMessage.getPort() + "\n\t";
        }
        return "";
    }

    /**
     * 请求日志打印的内容
     * @param requestLogMessage 请求日志字符串
     * @return 请求日志字符串
     */
    protected String requestLoggingContent(RequestLogMessage requestLogMessage) {
        StringBuilder builder = new StringBuilder();
        builder.append("Request: \n\t");
        builder.append(retryContent(requestLogMessage));
        builder.append(proxyContent(requestLogMessage));
        builder.append(requestTypeChangeHistory(requestLogMessage));
        builder.append(requestLogMessage.getRequestLine());
        String headers = requestLoggingHeaders(requestLogMessage);
        if (StringUtils.isNotEmpty(headers)) {
            builder.append("\n\tHeaders: \n");
            builder.append(headers);
        }
        String body = requestLoggingBody(requestLogMessage);
        if (StringUtils.isNotEmpty(body)) {
            builder.append("\n\tBody: ");
            builder.append(body);
        }
        return builder.toString();
    }

    /**
     * 请求响应日志打印的内容
     * @param responseLogMessage 请求响应日志字符串
     * @return 请求响应日志字符串
     */
    protected String responseLoggingContent(ResponseLogMessage responseLogMessage) {
        ForestResponse response = responseLogMessage.getResponse();
        if (response != null && response.getException() != null) {
            return "[Network Error]: " + response.getException().getMessage();
        }
        int status = responseLogMessage.getStatus();
        if (status >= 0) {
            return "Response: Status = " + responseLogMessage.getStatus() + ", Time = " + responseLogMessage.getTime() + "ms";
        } else {
            return "[Network Error]: Unknown Network Error!";
        }
    }


    /**
     * 打印日志内容
     * @param content 日志内容字符串
     */
    public void logContent(String content) {
        getLogger().info("[Forest] " + content);
    }


    @Override
    public ForestLogger getLogger() {
        return logger;
    }

    @Override
    public void setLogger(ForestLogger logger) {
        this.logger = logger;
    }

    /**
     * 打印请求日志
     * @param requestLogMessage 请求日志消息
     */
    @Override
    public void logRequest(RequestLogMessage requestLogMessage) {
        String content = requestLoggingContent(requestLogMessage);
        logContent(content);
    }

    /**
     * 打印响应状态日志
     * @param responseLogMessage 响应日志消息
     */
    @Override
    public void logResponseStatus(ResponseLogMessage responseLogMessage) {
        String content = responseLoggingContent(responseLogMessage);
        logContent(content);
    }

    /**
     * 打印响应内容日志
     * @param responseLogMessage 响应日志消息
     */
    @Override
    public void logResponseContent(ResponseLogMessage responseLogMessage) {
        if (responseLogMessage.getResponse() != null && responseLogMessage.getResponse().isSuccess()) {
            logContent("Response: Content=" + responseLogMessage.getResponse().getContent());
        }
    }
}
