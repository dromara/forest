package com.dtflys.forest.backend.httpclient.response;

import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.IOException;
import java.io.InputStream;
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
