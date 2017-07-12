package org.forest.executors.httpclient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.nio.util.ExpandableBuffer;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.forest.converter.json.ForestJsonConverter;
import org.forest.executors.BodyBuilder;
import org.forest.executors.httpclient.body.HttpclientNonBodyBuilder;
import org.forest.executors.httpclient.response.HttpclientForestResponseFactory;
import org.forest.executors.httpclient.response.HttpclientResponseHandler;
import org.forest.executors.httpclient.response.SyncHttpclientResponseHandler;
import org.forest.executors.url.URLBuilder;
import org.forest.handler.ResponseHandler;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.http.ForestResponseFactory;
import org.forest.utils.RequestNameValue;
import org.forest.exceptions.ForestNetworkException;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.executors.AbstractHttpExecutor;
import org.forest.mapping.MappingTemplate;
import org.forest.utils.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author gongjun
 * @since 2016-06-14
 */
public abstract class AbstractHttpclientExecutor<T extends  HttpRequestBase> extends AbstractHttpExecutor {
    private static Log log = LogFactory.getLog(AbstractHttpclientExecutor.class);

    protected HttpClient client = getHttpClient();
    protected HttpResponse httpResponse;
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


    private final static CookieSpecFactory defaultCookieSF = new CookieSpecFactory() {
        public CookieSpec newInstance(HttpParams params) {
            return new BrowserCompatSpec() {
                @Override
                public void validate(Cookie cookie, CookieOrigin origin)
                        throws MalformedCookieException {
                }
            };
        }
    };

    protected void prepareBodyBuilder() {
        bodyBuilder = new HttpclientNonBodyBuilder();
    }

    protected void prepare() {
        httpRequest = buildRequest();
        prepareBodyBuilder();
        prepareHeaders();
        prepareBody();
    }

    public AbstractHttpclientExecutor(HttpclientConnectionManager connectionManager, ForestRequest request, HttpclientResponseHandler httpclientResponseHandler) {
        super(connectionManager, request);
        this.typeName = request.getType().toUpperCase();
        this.httpclientResponseHandler = httpclientResponseHandler;
        prepare();
    }

    public void prepareHeaders() {
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonCoverter();
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

    protected HttpClient getHttpClient() {
        HttpClient client = connectionManager.getHttpClient();
        setupHttpClient(client);
        return client;
    }

    protected void setupHttpClient(HttpClient client) {
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, request.getTimeout());
        if (client instanceof DefaultHttpClient) {
            ((DefaultHttpClient) client).getCookieSpecs().register("default", defaultCookieSF);
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY, "default");
        }
    }

    protected static void logContent(String content) {
        log.info("[Forest] " + content);
    }

    public void logRequestBegine(int retryCount, T httpReq) {
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

    public static void logResponse(ForestRequest request, HttpClient client, HttpRequestBase httpReq, ForestResponse response) {
        if (!request.isLogEnable()) return;
        logContent("Response: Status=" + response.getStatusCode());
        if (response.isSuccess()) {
            logContent("Response: Content=" + response.getContent());
        }
    }

    public void execute(ResponseHandler responseHandler) {
        execute(0, responseHandler);
    }

    protected static ForestResponse sendRequest(ForestRequest request, HttpClient httpclient, HttpUriRequest httpUriRequest, HttpclientResponseHandler responseHandler)
            throws IOException {
        HttpResponse httpResponse = null;
        httpResponse = httpclient.execute(httpUriRequest);
        ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();
        ForestResponse response = forestResponseFactory.createResponse(request, httpResponse);
        logResponse(request, httpclient, (HttpRequestBase) httpUriRequest, response);
        try {
            responseHandler.handle(httpUriRequest, httpResponse, response);
        } catch (Exception ex) {
            if (ex instanceof ForestRuntimeException) {
                throw ex;
            }
            else {
                throw new ForestRuntimeException(ex);
            }
        }
        return response;
    }


    public void execute(int retryCount, ResponseHandler responseHandler) {
        try {
            logRequestBegine(retryCount, httpRequest);
            response = sendRequest(request, client, httpRequest, httpclientResponseHandler);
        } catch (IOException e) {
            if (retryCount >= request.getRetryCount()) {
                httpRequest.abort();
                ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();
                response = forestResponseFactory.createResponse(request, httpResponse);
                responseHandler.handle(request, response);
                throw new ForestRuntimeException(e);
            }
            log.error(e.getMessage());
            execute(retryCount + 1, responseHandler);
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
        client.getConnectionManager().closeExpiredConnections();
*/
    }


}
