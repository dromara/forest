package org.dromara.forest.backend.httpclient.response;

import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.http.ForestResponseFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.util.Date;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-12 17:07
 */
public class HttpclientForestResponseFactory implements ForestResponseFactory<HttpResponse> {

    private volatile ForestResponse<?> resultResponse;

    @Override
    public synchronized ForestResponse<?> createResponse(ForestRequest<?> request, HttpResponse httpResponse, LifeCycleHandler lifeCycleHandler, Throwable exception, Date requestTime) {
        if (resultResponse != null) {
            return resultResponse;
        }
        HttpEntity entity = null;
        if (httpResponse != null) {
            entity = httpResponse.getEntity();
            if (entity != null) {
                entity = new HttpclientEntity(request, entity, lifeCycleHandler);
            }
        }
        HttpclientForestResponse response = new HttpclientForestResponse(request, httpResponse, entity, requestTime, new Date());
        this.resultResponse = response;
        response.setException(exception);
        return response;
    }

}
