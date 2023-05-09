package org.dromara.forest.backend.httpclient.request;

import org.dromara.forest.backend.AbstractHttpExecutor;
import org.dromara.forest.backend.httpclient.response.HttpclientResponseHandler;
import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestRequest;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.Date;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 15:47
 */
public interface HttpclientRequestSender {

    void sendRequest(ForestRequest request,
                     AbstractHttpExecutor executor,
                     HttpclientResponseHandler responseHandler,
                     HttpUriRequest httpRequest,
                     LifeCycleHandler lifeCycleHandler,
                     Date startDate) throws IOException;

}
