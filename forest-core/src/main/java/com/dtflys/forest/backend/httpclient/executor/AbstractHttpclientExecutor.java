package com.dtflys.forest.backend.httpclient.executor;

import com.dtflys.forest.backend.AbstractHttpExecutor;
import com.dtflys.forest.backend.BodyBuilder;
import com.dtflys.forest.backend.httpclient.HttpclientRequestProvider;
import com.dtflys.forest.backend.httpclient.body.HttpclientBodyBuilder;
import com.dtflys.forest.backend.httpclient.logging.HttpclientLogBodyMessage;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import com.dtflys.forest.logging.LogBodyMessage;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.logging.ForestLogHandler;
import com.dtflys.forest.logging.LogHeaderMessage;
import com.dtflys.forest.logging.RequestLogMessage;
import com.dtflys.forest.logging.ResponseLogMessage;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.backend.httpclient.request.HttpclientRequestSender;
import com.dtflys.forest.backend.httpclient.response.HttpclientForestResponseFactory;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.mapping.MappingTemplate;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author gongjun
 * @since 2016-06-14
 */
public abstract class AbstractHttpclientExecutor<T extends  HttpRequestBase> extends AbstractHttpExecutor {
    private static Logger log = LoggerFactory.getLogger(AbstractHttpclientExecutor.class);

    protected final HttpclientResponseHandler httpclientResponseHandler;
    protected String url;
    protected final String typeName;
    protected T httpRequest;
    protected BodyBuilder<T> bodyBuilder;


    protected T buildRequest() {
        url = buildUrl();
        return getRequestProvider().getRequest(url);
    }

    protected abstract HttpclientRequestProvider<T> getRequestProvider();

    protected abstract URLBuilder getURLBuilder();


    protected String buildUrl() {
        return getURLBuilder().buildUrl(request);
    }


    protected void prepareBodyBuilder() {
        bodyBuilder = new HttpclientBodyBuilder();
    }

    protected void prepare(LifeCycleHandler lifeCycleHandler) {
        httpRequest = buildRequest();
        prepareBodyBuilder();
        prepareHeaders();
        prepareBody(lifeCycleHandler);
    }

    public AbstractHttpclientExecutor(ForestRequest request, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(request, requestSender);
        this.typeName = request.getType().getName();
        this.httpclientResponseHandler = httpclientResponseHandler;
    }

    public void prepareHeaders() {
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        List<RequestNameValue> headerList = request.getHeaderNameValueList();
        String contentType = request.getContentType();
        String contentEncoding = request.getContentEncoding();
        if (headerList != null && !headerList.isEmpty()) {
            for (RequestNameValue nameValue : headerList) {
                String name = nameValue.getName();
                if (!"Content-Type".equalsIgnoreCase(name)
                        && !"Content-Encoding".equalsIgnoreCase(name)) {
                    httpRequest.setHeader(name, MappingTemplate.getParameterValue(jsonConverter, nameValue.getValue()));
                }
            }
        }
        if (StringUtils.isNotEmpty(contentType) && !contentType.startsWith("multipart/form-data")) {
            httpRequest.setHeader("Content-Type", contentType);
        }
        if (StringUtils.isNotEmpty(contentEncoding)) {
            httpRequest.setHeader("Content-Encoding", contentEncoding);
        }
    }

    public void prepareBody(LifeCycleHandler lifeCycleHandler) {
        bodyBuilder.buildBody(httpRequest, request, lifeCycleHandler);
    }


    protected static void logContent(String content) {
        log.info("[Forest] " + content);
    }

    private RequestLogMessage getRequestLogMessage(int retryCount, T httpReq) {
        RequestLogMessage logMessage = new RequestLogMessage();
        URI uri = httpReq.getURI();
        logMessage.setUri(uri.toASCIIString());
        logMessage.setType(httpReq.getMethod());
        logMessage.setScheme(uri.getScheme());
        logMessage.setRetryCount(retryCount);
        setLogHeaders(logMessage, httpReq);
        setLogBody(logMessage, httpReq);
        return logMessage;
    }

    public void logRequest(int retryCount, T httpReq) {
        LogConfiguration logConfiguration = request.getLogConfiguration();
        if (!logConfiguration.isLogEnabled() || !logConfiguration.isLogRequest()) {
            return;
        }
        RequestLogMessage logMessage = getRequestLogMessage(retryCount, httpReq);
        logMessage.setRequest(request);
        request.setRequestLogMessage(logMessage);

        ForestLogHandler logHandler = request.getLogConfiguration().getLogHandler();
        if (logHandler != null) {
            logHandler.logRequest(logMessage);
        }
    }

    public void logResponse(long startTime, ForestResponse response) {
        LogConfiguration logConfiguration = request.getLogConfiguration();
        if (!logConfiguration.isLogEnabled()) {
            return;
        }
        long endTime = System.currentTimeMillis();
        ResponseLogMessage logMessage = new ResponseLogMessage(response, startTime, endTime, response.getStatusCode());
        ForestLogHandler logHandler = logConfiguration.getLogHandler();
        if (logHandler != null) {
            if (logConfiguration.isLogResponseStatus()) {
                logHandler.logResponseStatus(logMessage);
            }
            if (logConfiguration.isLogResponseContent() && logConfiguration.isLogResponseContent()) {
                logHandler.logResponseContent(logMessage);
            }
        }
    }


    protected void setLogHeaders(RequestLogMessage logMessage, T httpReq) {
        Header[] headers = httpReq.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            Header header = headers[i];
            String name = header.getName();
            String value = header.getValue();
            logMessage.addHeader(new LogHeaderMessage(name, value));
        }
    }

    protected void setLogBody(RequestLogMessage logMessage, T httpReq) {
        if (!(httpReq instanceof HttpEntityEnclosingRequestBase)) {
            return;
        }
        HttpEntityEnclosingRequestBase entityEnclosingRequest = (HttpEntityEnclosingRequestBase) httpReq;
        HttpEntity entity = entityEnclosingRequest.getEntity();
        if (entity == null) {
            return;
        }
        LogBodyMessage logBodyMessage = new HttpclientLogBodyMessage(entity);
        logMessage.setBody(logBodyMessage);
    }


    private String getLogContentForStringBody(HttpEntity entity) {
        InputStream in = null;
        try {
            in = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            return getLogContentFormBufferedReader(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getLogContentFormBufferedReader(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;
        String body;
        List<String> lines = new LinkedList<>();
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        for (int i = 0, len = lines.size(); i < len; i++) {
            builder.append(lines.get(i));
            if (i < len - 1) {
                builder.append("\\n");
            }
        }
        body = builder.toString();
        return body;
    }

    @Override
    public void execute(LifeCycleHandler lifeCycleHandler) {
        prepare(lifeCycleHandler);
        execute(0, lifeCycleHandler);
    }


    public void execute(int retryCount, LifeCycleHandler lifeCycleHandler) {
        Date startDate = new Date();
        long startTime = startDate.getTime();
        try {
            logRequest(retryCount, httpRequest);
            requestSender.sendRequest(request, httpclientResponseHandler, httpRequest, lifeCycleHandler, startTime, 0);
        } catch (IOException e) {
            if (retryCount >= request.getRetryCount()) {
                httpRequest.abort();
                ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();
                response = forestResponseFactory.createResponse(request, null, lifeCycleHandler);
                logResponse(startTime, response);
                lifeCycleHandler.handleSyncWitchException(request, response, e);
//                throw new ForestRuntimeException(e);
                return;
            }
            log.error(e.getMessage());
//            execute(retryCount + 1, lifeCycleHandler);
        } catch (ForestRuntimeException e) {
            httpRequest.abort();
            throw e;
        }
    }

    @Override
    public void close() {
/*
        if (httpResponse != null) {
            try {
                EntityUtils.consume(httpResponse.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        client0.getConnectionManager().closeExpiredConnections();
*/
    }


}
