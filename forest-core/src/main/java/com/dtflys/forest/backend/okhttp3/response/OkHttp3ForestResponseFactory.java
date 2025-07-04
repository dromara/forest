package com.dtflys.forest.backend.okhttp3.response;

import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import com.dtflys.forest.http.UnclosedResponse;
import com.dtflys.forest.utils.ReflectUtils;
import okhttp3.Response;

import java.util.Date;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 15:20
 */
public class OkHttp3ForestResponseFactory implements ForestResponseFactory<Response> {

    @Override
    public ForestResponse<?> createResponse(ForestRequest<?> request, Response res, LifeCycleHandler lifeCycleHandler, Throwable exception, Date requestTime) {
        final ForestResponse<?> response = isUnclosedResponse(request, lifeCycleHandler) ?
                new OkHttp3UnclosedResponse(request, res, requestTime, new Date()) :
                new OkHttp3ForestResponse(request, res, requestTime, new Date());
        response.setException(exception);
        return response;
    }
}
