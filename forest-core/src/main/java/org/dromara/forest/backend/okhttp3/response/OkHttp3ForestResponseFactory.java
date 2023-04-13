package org.dromara.forest.backend.okhttp3.response;

import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.http.ForestResponseFactory;
import okhttp3.Response;

import java.util.Date;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 15:20
 */
public class OkHttp3ForestResponseFactory implements ForestResponseFactory<Response> {

    @Override
    public ForestResponse<?> createResponse(ForestRequest<?> request, Response res, LifeCycleHandler lifeCycleHandler, Throwable exception, Date requestTime) {
        ForestResponse<?> response = new OkHttp3ForestResponse(request, res, requestTime, new Date());
        response.setException(exception);
        return response;
    }
}
