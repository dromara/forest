package org.dromara.forest.backend.httpclient;

import org.dromara.forest.backend.AbstractHttpBackend;
import org.dromara.forest.backend.ForestConnectionManager;
import org.dromara.forest.backend.HttpExecutor;
import org.dromara.forest.backend.httpclient.conn.HttpclientConnectionManager;
import org.dromara.forest.backend.httpclient.request.HttpclientRequestSender;
import org.dromara.forest.backend.httpclient.request.SyncHttpclientRequestSender;
import org.dromara.forest.backend.httpclient.response.HttpclientResponseHandler;
import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.backend.httpclient.executor.HttpclientExecutor;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 18:23
 */
public class HttpclientBackend extends AbstractHttpBackend {

    public final static String NAME = "httpclient";

    private static final String HTTPCLIENT_REQUEST_KEY = "#httpclient_request";


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isAllowEncodeBraceInQueryValue() {
        return true;
    }

    @Override
    public HttpExecutor createSyncExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        return new HttpclientExecutor(request,
                getHttpclientResponseHandler(request, lifeCycleHandler),
                getRequestSender(connectionManager, request));
    }


    private static HttpclientResponseHandler getHttpclientResponseHandler(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        return new HttpclientResponseHandler(request, lifeCycleHandler);
    }

    @SuppressWarnings("deprecation")
    private static HttpclientRequestSender getRequestSender(ForestConnectionManager connectionManager, ForestRequest request) {
//        if (request.isAsync()) {
//            return new AsyncHttpclientRequestSender((HttpclientConnectionManager) connectionManager, request);
//        }
        return new SyncHttpclientRequestSender((HttpclientConnectionManager) connectionManager, request);
    }

    public HttpclientBackend() {
        super(new HttpclientConnectionManager());
    }

}
