package com.dtflys.forest.backend;

import com.dtflys.forest.backend.httpclient.request.HttpclientRequestSender;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

/**
 * @author gongjun
 * @since 2016-05-18
 */
public abstract class AbstractHttpExecutor implements HttpExecutor {

    protected final ForestRequest request;

    protected HttpclientRequestSender requestSender;

    protected ForestResponse response;


    public AbstractHttpExecutor(ForestRequest request, HttpclientRequestSender requestSender) {
        this.request = request;
        this.requestSender = requestSender;
    }

    @Override
    public ForestRequest getRequest() {
        return request;
    }

    @Override
    public abstract ForestResponse<?> execute(LifeCycleHandler lifeCycleHandler);

    @Override
    public abstract void close();

}
