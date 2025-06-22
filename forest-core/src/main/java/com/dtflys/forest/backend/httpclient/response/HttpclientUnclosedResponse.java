package com.dtflys.forest.backend.httpclient.response;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.UnclosedResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.util.Date;

public class HttpclientUnclosedResponse extends HttpclientForestResponse implements UnclosedResponse {

    public HttpclientUnclosedResponse(ForestRequest request, HttpResponse httpResponse, HttpEntity entity, Date requestTime, Date responseTime) {
        super(request, httpResponse, entity, requestTime, responseTime, false);
    }
}
