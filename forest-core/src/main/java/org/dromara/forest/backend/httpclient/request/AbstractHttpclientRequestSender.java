package org.dromara.forest.backend.httpclient.request;

import org.dromara.forest.backend.httpclient.HttpclientCookie;
import org.dromara.forest.backend.httpclient.conn.HttpclientConnectionManager;
import org.dromara.forest.backend.httpclient.logging.HttpclientLogBodyMessage;
import org.dromara.forest.http.ForestCookie;
import org.dromara.forest.http.ForestCookies;
import org.dromara.forest.http.ForestHeaderMap;
import org.dromara.forest.http.ForestProxy;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.logging.ForestLogHandler;
import org.dromara.forest.logging.LogBodyMessage;
import org.dromara.forest.logging.LogConfiguration;
import org.dromara.forest.logging.LogHeaderMessage;
import org.dromara.forest.logging.RequestLogMessage;
import org.dromara.forest.logging.RequestProxyLogMessage;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;

import java.net.URI;


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


    private <T extends  HttpRequestBase> RequestLogMessage getRequestLogMessage(int retryCount, T httpReq, HttpClient client) {
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
            ForestHeaderMap headers = proxy.getHeaders();
            if (headers != null && !headers.isEmpty()) {
                proxyLogMessage.setHeaders(
                        proxy.getHeaders().entrySet().stream()
                                .map(entry -> entry.getKey() + ": " + entry.getValue())
                                .toArray(String[]::new));
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
        RequestLogMessage logMessage = getRequestLogMessage(retryCount, httpReq, client);
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
