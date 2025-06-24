package com.dtflys.forest.backend.okhttp3.response;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.UnclosedResponse;
import okhttp3.Response;

import java.util.Date;

public class OkHttp3UnclosedResponse extends OkHttp3ForestResponse implements UnclosedResponse {
    
    public OkHttp3UnclosedResponse(ForestRequest request, Response okResponse, Date requestTime, Date responseTime) {
        super(request, okResponse, requestTime, responseTime, false);
    }
}
