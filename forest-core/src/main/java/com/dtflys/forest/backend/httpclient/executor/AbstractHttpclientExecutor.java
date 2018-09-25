package com.dtflys.forest.backend.httpclient.executor;

import com.dtflys.forest.backend.AbstractHttpExecutor;
import com.dtflys.forest.backend.BodyBuilder;
import com.dtflys.forest.backend.NoneBodyBuilder;
import com.dtflys.forest.backend.httpclient.HttpclientRequestProvider;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponseFactory;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.backend.BodyBuilder;
import com.dtflys.forest.backend.NoneBodyBuilder;
import com.dtflys.forest.backend.httpclient.request.HttpclientRequestSender;
import com.dtflys.forest.backend.httpclient.response.HttpclientForestResponseFactory;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.handler.ResponseHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponseFactory;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.backend.AbstractHttpExecutor;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.utils.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author gongjun
 * @since 2016-06-14
 */
public abstract class AbstractHttpclientExecutor<T extends  HttpRequestBase> extends AbstractHttpExecutor {
    private static Log log = LogFactory.getLog(AbstractHttpclientExecutor.class);

    protected final HttpclientResponseHandler httpclientResponseHandler;
    protected String url = buildUrl();
    protected final String typeName;
    protected T httpRequest;
    protected BodyBuilder<T> bodyBuilder;


    protected T buildRequest() {
        return getRequestProvider().getRequest(url);
    }

    protected abstract HttpclientRequestProvider<T> getRequestProvider();

    protected abstract URLBuilder getURLBuilder();


    protected String buildUrl() {
        return getURLBuilder().buildUrl(request);
    }


    protected void prepareBodyBuilder() {
        bodyBuilder = new NoneBodyBuilder();
    }

    protected void prepare() {
        httpRequest = buildRequest();
        prepareBodyBuilder();
        prepareHeaders();
        prepareBody();
    }

    public AbstractHttpclientExecutor(ForestRequest request, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(request, requestSender);
        this.typeName = request.getType().toUpperCase();
        this.httpclientResponseHandler = httpclientResponseHandler;
        prepare();
    }

    public void prepareHeaders() {
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        List<RequestNameValue> headerList = request.getHeaderNameValueList();
        if (headerList != null && !headerList.isEmpty()) {
            for (RequestNameValue nameValue : headerList) {
                httpRequest.setHeader(nameValue.getName(), MappingTemplate.getParameterValue(jsonConverter, nameValue.getValue()));
            }
        }
    }

    public void prepareBody() {
        bodyBuilder.buildBody(httpRequest, request);
    }


    protected static void logContent(String content) {
        log.info("[Forest] " + content);
    }

    public void logRequest(int retryCount, T httpReq) {
        if (!request.isLogEnable()) return;
        String requestLine = getLogContentForRequestLine(retryCount, httpReq);
        String headers = getLogContentForHeaders(httpReq);
        String body = getLogContentForBody(httpReq);
        String content = "Request: \n\t" + requestLine;
        if (StringUtils.isNotEmpty(headers)) {
            content += "\n\tHeaders: \n" + headers;
        }
        if (StringUtils.isNotEmpty(body)) {
            content += "\n\tBody: " + body;
        }
        logContent(content);
    }

    protected String getLogContentForRequestLine(int retryCount, T httpReq) {
        if (retryCount == 0) {
            return httpReq.getRequestLine().toString();
        }
        else {
            return "[Retry: " + retryCount + "] " + httpReq.getRequestLine().toString();
        }
    }

    protected String getLogContentForHeaders(T httpReq) {
        StringBuffer buffer = new StringBuffer();
        Header[] headers = httpReq.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            Header header = headers[i];
            buffer.append("\t\t" + header.getName() + ": " + header.getValue());
            if (i < headers.length - 1) {
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }

    protected String getLogContentForBody(T httpReq) {
        return null;
    }


    public void execute(ResponseHandler responseHandler) {
        execute(0, responseHandler);
    }


    public void execute(int retryCount, ResponseHandler responseHandler) {
        try {
            logRequest(retryCount, httpRequest);
            requestSender.sendRequest(request, httpclientResponseHandler, httpRequest);
        } catch (IOException e) {
            if (retryCount >= request.getRetryCount()) {
                httpRequest.abort();
                ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();
                response = forestResponseFactory.createResponse(request, null);
                responseHandler.handleSyncWitchException(request, response, e);
//                throw new ForestRuntimeException(e);
                return;
            }
            log.error(e.getMessage());
            execute(retryCount + 1, responseHandler);
        } catch (ForestRuntimeException e) {
            httpRequest.abort();
            throw e;
        }
    }

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
