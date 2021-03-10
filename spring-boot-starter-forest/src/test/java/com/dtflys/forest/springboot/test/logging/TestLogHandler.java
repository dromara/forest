package com.dtflys.forest.springboot.test.logging;

import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.RequestLogMessage;
import com.dtflys.forest.logging.ResponseLogMessage;
import com.dtflys.forest.utils.StringUtils;

public class TestLogHandler extends DefaultLogHandler {

    @Override
    public void logContent(String content) {
        super.logContent("[Test1] " + content);
    }

    /**
     * 该方法生成Forest请求的日志内容字符串
     * @param requestLogMessage 请求日志字符串
     * @return 日志内容字符串
     */
    @Override
    protected String requestLoggingContent(RequestLogMessage requestLogMessage) {
        StringBuilder builder = new StringBuilder();
        builder.append("请求: \n\t");
        builder.append(retryContent(requestLogMessage));
        builder.append(proxyContent(requestLogMessage));
        builder.append(requestTypeChangeHistory(requestLogMessage));
        builder.append(requestLogMessage.getRequestLine());
        String headers = requestLoggingHeaders(requestLogMessage);
        if (StringUtils.isNotEmpty(headers)) {
            builder.append("\n\t请求头: \n");
            builder.append(headers);
        }
        String body = requestLoggingBody(requestLogMessage);
        if (StringUtils.isNotEmpty(body)) {
            builder.append("\n\t请求体: \n");
            builder.append(body);
        }
        return builder.toString();

    }

    /**
     * 该方法生成Forest请求响应结果的日志内容字符串
     * @param responseLogMessage 请求响应日志字符串
     * @return 日志内容字符串
     */
    @Override
    protected String responseLoggingContent(ResponseLogMessage responseLogMessage) {
        ForestResponse response = responseLogMessage.getResponse();
        if (response != null && response.getException() != null) {
            return "[网络错误]: " + response.getException().getMessage();
        }
        int status = responseLogMessage.getStatus();
        if (status >= 0) {
            return "请求响应: 状态码: " + responseLogMessage.getStatus() + ", 耗时: " + responseLogMessage.getTime() + "ms";
        } else {
            return "[网络错误]: 未知的网络错误!";
        }

    }
}
