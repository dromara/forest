package com.dtflys.forest.backend.httpclient.request;

import com.dtflys.forest.backend.AbstractHttpExecutor;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.Date;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 15:47
 */
public interface HttpclientRequestSender {

    ForestResponse<?> sendRequest(ForestRequest request,
                               AbstractHttpExecutor executor,
                               HttpclientResponseHandler responseHandler,
                               HttpUriRequest httpRequest,
                               LifeCycleHandler lifeCycleHandler,
                               Date startDate) throws IOException;

}
