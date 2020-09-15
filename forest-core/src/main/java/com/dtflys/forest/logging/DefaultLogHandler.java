package com.dtflys.forest.logging;

import com.dtflys.forest.utils.StringUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-14 17:31
 */
public class DefaultLogHandler implements LogHandler {

    private final static Logger logger = LoggerFactory.getLogger(DefaultLogHandler.class);

    protected String requestLoggingHeaders(RequestLogMessage requestLogMessage) {
        String headers = "";
        return headers;
    }

    protected String requestLoggingBody(RequestLogMessage requestLogMessage) {
        String body = "";
        return body;
    }

    protected String requestLoggingContent(RequestLogMessage requestLogMessage) {
        String content = "Request: \n\t" + requestLogMessage.getRequestLine();
        String headers = requestLoggingHeaders(requestLogMessage);
        if (StringUtils.isNotEmpty(headers)) {
            content += "\n\tHeaders: \n" + headers;
        }
        String body = requestLoggingBody(requestLogMessage);
        if (StringUtils.isNotEmpty(body)) {
            content += "\n\tBody: " + body;
        }
        return content;
    }

    protected static void logContent(String content) {
        logger.info("[Forest] " + content);
    }


    @Override
    public void logRequest(RequestLogMessage requestLogMessage) {
        String content = requestLoggingContent(requestLogMessage);
        logContent(content);
    }

    @Override
    public void logResponse(ResponseLogMessage responseLogMessage) {

    }
}
