package com.dtflys.forest.backend.httpclient;

import com.dtflys.forest.backend.AbstractHttpBackend;
import com.dtflys.forest.backend.ForestConnectionManager;
import com.dtflys.forest.backend.HttpExecutor;
import com.dtflys.forest.backend.httpclient.conn.HttpclientConnectionManager;
import com.dtflys.forest.backend.httpclient.executor.*;
import com.dtflys.forest.backend.httpclient.request.HttpclientRequestSender;
import com.dtflys.forest.backend.httpclient.request.SyncHttpclientRequestSender;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 18:23
 */
public class HttpclientBackend extends AbstractHttpBackend {

    public final static String NAME = "httpclient";

    @Override
    public String getName() {
        return NAME;
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
