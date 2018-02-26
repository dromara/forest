package org.forest.backend.httpclient.response;

import org.apache.http.HttpResponse;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-26 15:37
 */
public class HttpclientForestResponse extends ForestResponse {

    private HttpResponse httpResponse;

    public HttpclientForestResponse(ForestRequest request, HttpResponse httpResponse) {
        super(request);
        this.httpResponse = httpResponse;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
}
