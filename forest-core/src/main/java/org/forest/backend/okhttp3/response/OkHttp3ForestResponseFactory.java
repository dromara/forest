package org.forest.backend.okhttp3.response;

import okhttp3.Response;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.http.ForestResponseFactory;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 15:20
 */
public class OkHttp3ForestResponseFactory implements ForestResponseFactory<Response> {

    @Override
    public ForestResponse createResponse(ForestRequest request, Response res) {
        ForestResponse response = new OkHttp3ForestResponse(request, res);
        return response;
    }
}
