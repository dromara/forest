package com.dtflys.forest.backend.okhttp3.response;

import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import okhttp3.Response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 15:20
 */
public class OkHttp3ForestResponseFactory implements ForestResponseFactory<Response> {

    @Override
    public ForestResponse createResponse(ForestRequest request, Response res, LifeCycleHandler lifeCycleHandler) {
        ForestResponse response = new OkHttp3ForestResponse(request, res);
        return response;
    }
}
