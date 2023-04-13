package org.dromara.forest.backend;

import org.dromara.forest.backend.httpclient.request.HttpclientRequestSender;
import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;

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
    public abstract void execute(LifeCycleHandler lifeCycleHandler);

    @Override
    public abstract void close();

}
