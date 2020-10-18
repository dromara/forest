package com.dtflys.forest.springboot.test.logging;

import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.RequestLogMessage;
import com.dtflys.forest.logging.ResponseLogMessage;

public class TestLogHandler extends DefaultLogHandler {

    @Override
    public void logContent(String content) {
        super.logContent("[Test] " + content);
    }

    /**
     * 该方法生成Forest请求的日志内容字符串
     * @param requestLogMessage 请求日志字符串
     * @return 日志内容字符串
     */
    @Override
    protected String requestLoggingContent(RequestLogMessage requestLogMessage) {
        return super.requestLoggingContent(requestLogMessage);
    }

    /**
     * 该方法生成Forest请求响应结果的日志内容字符串
     * @param responseLogMessage 请求响应日志字符串
     * @return 日志内容字符串
     */
    @Override
    protected String responseLoggingContent(ResponseLogMessage responseLogMessage) {
        return super.responseLoggingContent(responseLogMessage);
    }
}
