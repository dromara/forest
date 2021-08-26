package com.dtflys.forest.backend.httpclient.request;

import com.dtflys.forest.backend.httpclient.conn.HttpclientConnectionManager;
import com.dtflys.forest.backend.httpclient.logging.HttpclientLogBodyMessage;
import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestCookies;
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
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-07-21 15:53
 */
public abstract class AbstractHttpclientRequestSender implements HttpclientRequestSender {
    private static Logger log = LoggerFactory.getLogger(AbstractHttpclientRequestSender.class);

    protected final HttpclientConnectionManager connectionManager;

    protected final ForestRequest request;

    public AbstractHttpclientRequestSender(HttpclientConnectionManager connectionManager, ForestRequest request) {
        this.connectionManager = connectionManager;
        this.request = request;
    }

    protected <T extends  HttpRequestBase> void setLogHeaders(RequestLogMessage logMessage, T httpReq) {
        Header[] headers = httpReq.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            Header header = headers[i];
            String name = header.getName();
            String value = header.getValue();
            logMessage.addHeader(new LogHeaderMessage(name, value));
        }
    }

    protected <T extends  HttpRequestBase> void setLogBody(RequestLogMessage logMessage, T httpReq) {
        HttpEntityEnclosingRequestBase entityEnclosingRequest = (HttpEntityEnclosingRequestBase) httpReq;
        HttpEntity entity = entityEnclosingRequest.getEntity();
        if (entity == null) {
            return;
        }
        LogBodyMessage logBodyMessage = new HttpclientLogBodyMessage(entity);
        logMessage.setBody(logBodyMessage);
    }


    private <T extends  HttpRequestBase> RequestLogMessage getRequestLogMessage(int retryCount, T httpReq) {
        RequestLogMessage logMessage = new RequestLogMessage();
        URI uri = httpReq.getURI();
        logMessage.setUri(uri.toASCIIString());
        logMessage.setType(httpReq.getMethod());
        logMessage.setScheme(uri.getScheme());
        logMessage.setRetryCount(retryCount);
        setLogHeaders(logMessage, httpReq);
        setLogBody(logMessage, httpReq);
        ForestProxy proxy = request.getProxy();
        if (proxy != null) {
            RequestProxyLogMessage proxyLogMessage = new RequestProxyLogMessage();
            proxyLogMessage.setHost(proxy.getHost());
            proxyLogMessage.setPort(proxy.getPort() + "");
        }
        return logMessage;
    }


    public <T extends  HttpRequestBase> void logRequest(int retryCount, T httpReq) {
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

    protected ForestCookies getCookiesFromHttpCookieStore(CookieStore cookieStore) {
        ForestCookies cookies = new ForestCookies();
        for (Cookie httpCookie : cookieStore.getCookies()) {
            ForestCookie cookie = ForestCookie.createFromHttpclientCookie(httpCookie);
            cookies.addCookie(cookie);
        }
        return cookies;
    }

}
