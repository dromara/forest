package org.forest.executors.httpclient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.forest.converter.json.ForestJsonConverter;
import org.forest.executors.BodyBuilder;
import org.forest.executors.httpclient.body.HttpclientNonBodyBuilder;
import org.forest.executors.url.URLBuilder;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
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
    protected HttpResponse httpResponse = null;
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
        setupHeaders();
        setupBody();
    }

    public AbstractHttpclientExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
        super(connectionManager, request);
        typeName = request.getType().toUpperCase();
        prepare();
    }

    public void setupHeaders() {
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonCoverter();
        List<RequestNameValue> headerList = request.getHeaderNameValueList();
        if (headerList != null && !headerList.isEmpty()) {
            for (RequestNameValue nameValue : headerList) {
                httpRequest.setHeader(nameValue.getName(), MappingTemplate.getParameterValue(jsonConverter, nameValue.getValue()));
            }
        }
    }

    public void setupBody() {
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

    protected void logContent(String content) {
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

    public void logResponse(int retryCount, HttpClient client, HttpRequestBase httpReq, ForestResponse response) {
        if (!request.isLogEnable()) return;
        logContent("Response: Status=" + response.getStatusCode());
    }

    public void execute() {
        execute(0);
    }


    public void execute(int retryCount) {
        try {
            logRequestBegine(retryCount, httpRequest);
            HttpResponse httpResponse = client.execute(httpRequest);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            ForestResponse response = forestResponseFactory.createResponse(request, httpResponse);
            logResponse(retryCount, client, httpRequest, response);
            setResponse(response);
            if (response.isError()) {
                throw new ForestNetworkException(httpResponse.getStatusLine().getReasonPhrase(), statusCode);
            }
        } catch (IOException e) {
            if (retryCount >= request.getRetryCount()) {
                httpRequest.abort();
                throw new ForestRuntimeException(e);
            }
            log.error(e.getMessage());
            execute(retryCount + 1);
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
