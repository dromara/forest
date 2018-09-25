package com.dtflys.forest.backend.httpclient.request;

import com.dtflys.forest.backend.httpclient.conn.HttpclientConnectionManager;
import com.dtflys.forest.backend.httpclient.response.HttpclientForestResponseFactory;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.*;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.HttpParams;

import java.io.IOException;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 20:16
 */
@Deprecated
public class SyncHttpclientRequestSender extends AbstractHttpclientRequestSender {

    private HttpClient client;

    public SyncHttpclientRequestSender(HttpclientConnectionManager connectionManager, ForestRequest request) {
        super(connectionManager, request);
    }

    protected HttpClient getHttpClient() {
        HttpClient client = connectionManager.getHttpClient(request);
        setupHttpClient(client);
        return client;
    }

    protected void setupHttpClient(HttpClient client) {
//        client0.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, request.getTimeout());
//        if (client0 instanceof DefaultHttpClient) {
//            ((DefaultHttpClient) client0).getCookieSpecs().register("default", defaultCookieSF);
//            client0.getParams().setParameter(ClientPNames.COOKIE_POLICY, "default");
//        }
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



    public static void logResponse(ForestRequest request, ForestResponse response) {
        if (!request.isLogEnable()) return;
        logContent("Response: Status=" + response.getStatusCode());
        if (response.isSuccess()) {
            logContent("Response: Content=" + response.getContent());
        }
    }


    @Override
    public void sendRequest(ForestRequest request, HttpclientResponseHandler responseHandler, HttpUriRequest httpRequest)
            throws IOException {
        HttpResponse httpResponse = null;
        ForestResponse response = null;
        client = getHttpClient();
        try {
            httpResponse = client.execute(httpRequest);
            ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();
            response = forestResponseFactory.createResponse(request, httpResponse);
            logResponse(request, response);
        } finally {
            connectionManager.afterConnect();
        }
        try {

            responseHandler.handleSync(httpResponse, response);
        } catch (Exception ex) {
            if (ex instanceof ForestRuntimeException) {
                throw ex;
            }
            else {
                throw new ForestRuntimeException(ex);
            }
        }
    }

}
