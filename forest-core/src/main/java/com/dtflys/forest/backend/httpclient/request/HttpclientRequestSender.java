package com.dtflys.forest.backend.httpclient.request;

import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.Date;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 15:47
 */
public interface HttpclientRequestSender {

    void sendRequest(ForestRequest request,
                     HttpclientResponseHandler responseHandler,
                     HttpUriRequest httpRequest,
                     LifeCycleHandler lifeCycleHandler,
                     CookieStore cookieStore,
                     Date startDate,
                     int retryCount) throws IOException;

}
