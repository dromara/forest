package com.dtflys.forest.backend.httpclient;

import com.dtflys.forest.backend.AbstractHttpBackend;
import com.dtflys.forest.backend.ForestConnectionManager;
import com.dtflys.forest.backend.HttpExecutor;
import com.dtflys.forest.backend.httpclient.conn.HttpclientConnectionManager;
import com.dtflys.forest.backend.httpclient.executor.*;
import com.dtflys.forest.backend.httpclient.request.AsyncHttpclientRequestSender;
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
    protected HttpExecutor createHeadExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        return new HttpclientHeadExecutor(request,
                getHttpclientResponseHandler(request, lifeCycleHandler),
                getRequestSender(connectionManager, request));
    }

    @Override
    protected HttpExecutor createGetExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        return new HttpclientGetExecutor(request,
                getHttpclientResponseHandler(request, lifeCycleHandler),
                getRequestSender(connectionManager, request));
    }

    @Override
    protected HttpExecutor createPostExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        return new HttpclientPostExecutor(request,
                getHttpclientResponseHandler(request, lifeCycleHandler),
                getRequestSender(connectionManager, request));

    }

    @Override
    protected HttpExecutor createPutExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        return new HttpclientPutExecutor(request,
                getHttpclientResponseHandler(request, lifeCycleHandler),
                getRequestSender(connectionManager, request));

    }

    @Override
    protected HttpExecutor createDeleteExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        return new HttpclientDeleteExecutor(request,
                getHttpclientResponseHandler(request, lifeCycleHandler),
                getRequestSender(connectionManager, request));

    }

    @Override
    protected HttpExecutor createOptionsExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        return new HttpclientOptionsExecutor(request,
                getHttpclientResponseHandler(request, lifeCycleHandler),
                getRequestSender(connectionManager, request));
    }

    @Override
    protected HttpExecutor createTraceExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        return new HttpclientTraceExecutor(request,
                getHttpclientResponseHandler(request, lifeCycleHandler),
                getRequestSender(connectionManager, request));
    }

    @Override
    protected HttpExecutor createPatchExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        return new HttpclientPatchExecutor(request,
                getHttpclientResponseHandler(request, lifeCycleHandler),
                getRequestSender(connectionManager, request));
    }


    private static HttpclientResponseHandler getHttpclientResponseHandler(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        return new HttpclientResponseHandler(request, lifeCycleHandler);
    }

    @SuppressWarnings("deprecation")
    private static HttpclientRequestSender getRequestSender(ForestConnectionManager connectionManager, ForestRequest request) {
        if (request.isAsync()) {
            return new AsyncHttpclientRequestSender((HttpclientConnectionManager) connectionManager, request);
        }
        return new SyncHttpclientRequestSender((HttpclientConnectionManager) connectionManager, request);
    }


    public HttpclientBackend() {
        super(new HttpclientConnectionManager());
    }





}
