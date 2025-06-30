package com.dtflys.forest.backend.httpclient.request;

import com.dtflys.forest.backend.httpclient.HttpclientCookie;
import com.dtflys.forest.backend.httpclient.conn.HttpclientConnectionManager;
import com.dtflys.forest.backend.httpclient.logging.HttpclientLogBodyMessage;
import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestHeaderMap;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.logging.ForestLogHandler;
import com.dtflys.forest.logging.LogBodyMessage;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.logging.LogHeaderMessage;
import com.dtflys.forest.logging.RequestLogMessage;
import com.dtflys.forest.logging.RequestProxyLogMessage;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;

import java.net.URI;
import java.util.stream.Collectors;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-07-21 15:53
 */
public abstract class AbstractHttpclientRequestSender implements HttpclientRequestSender {

    protected final HttpclientConnectionManager connectionManager;

    protected final ForestRequest request;

    public AbstractHttpclientRequestSender(HttpclientConnectionManager connectionManager, ForestRequest request) {
        this.connectionManager = connectionManager;
        this.request = request;
    }

    protected <T extends  HttpRequestBase> void setLogHeaders(RequestLogMessage logMessage, T httpReq) {
        final Header[] headers = httpReq.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            final Header header = headers[i];
            final String name = header.getName();
            final String value = header.getValue();
            logMessage.addHeader(new LogHeaderMessage(name, value));
        }
    }

    protected <T extends  HttpRequestBase> void setLogBody(RequestLogMessage logMessage, T httpReq) {
        final HttpEntityEnclosingRequestBase entityEnclosingRequest = (HttpEntityEnclosingRequestBase) httpReq;
        final HttpEntity entity = entityEnclosingRequest.getEntity();
        if (entity == null) {
            return;
        }
        final LogBodyMessage logBodyMessage = new HttpclientLogBodyMessage(entity);
        logMessage.setBody(logBodyMessage);
    }


    private <T extends  HttpRequestBase> RequestLogMessage getRequestLogMessage(
            LogConfiguration logConfiguration, int retryCount, T httpReq, HttpClient client) {
        final RequestLogMessage logMessage = new RequestLogMessage();
        final URI uri = httpReq.getURI();
        logMessage.setUri(uri.toASCIIString());
        logMessage.setType(httpReq.getMethod());
        logMessage.setScheme(uri.getScheme());
        logMessage.setRetryCount(retryCount);
        setLogHeaders(logMessage, httpReq);
        setLogBody(logMessage, httpReq);
        final ForestProxy proxy = request.getProxy();

        if (proxy != null) {
            final RequestProxyLogMessage proxyLogMessage = new RequestProxyLogMessage();
            proxyLogMessage.setType(proxy.getType().name());
            proxyLogMessage.setHost(proxy.getHost());
            proxyLogMessage.setPort(proxy.getPort() + "");
            if (logConfiguration.isLogRequestHeaders()) {
                final ForestHeaderMap headers = proxy.getHeaders();
                if (headers != null && !headers.isEmpty()) {
                    proxyLogMessage.setHeaders(
                            proxy.getHeaders().entrySet().stream()
                                    .map(entry -> entry.getKey() + ": " + entry.getValue())
                                    .toArray(String[]::new));
                }
            }
            logMessage.setProxy(proxyLogMessage);
        }
        return logMessage;
    }


    public <T extends  HttpRequestBase> void logRequest(int retryCount, T httpReq, HttpClient client) {

        LogConfiguration logConfiguration = request.getLogConfiguration();
        if (!logConfiguration.isLogEnabled() || !logConfiguration.isLogRequest()) {
            return;
        }
        RequestLogMessage logMessage = getRequestLogMessage(logConfiguration, retryCount, httpReq, client);
        logMessage.setRequest(request);
        request.setRequestLogMessage(logMessage);

        ForestLogHandler logHandler = request.getLogConfiguration().getLogHandler();
        if (logHandler != null) {
            logHandler.logRequest(logMessage);
        }
    }

    protected ForestCookies getCookiesFromHttpCookieStore(CookieStore cookieStore) {
        ForestCookies cookies = new ForestCookies();
        for (Cookie httpCookie : cookieStore.getCookies()) {
            ForestCookie cookie = new HttpclientCookie(httpCookie);
            cookies.addCookie(cookie);
        }
        return cookies;
    }

}
