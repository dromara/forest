package org.forest.executors.httpclient.response;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.http.ForestResponseFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-12 17:07
 */
public class HttpclientForestResponseFactory implements ForestResponseFactory<HttpResponse> {

    private String responseContent;

    private String getString(String encode, HttpEntity entity) throws IOException {
        if (responseContent == null) {
            InputStream inputStream = entity.getContent();
            responseContent = IOUtils.toString(inputStream, encode);
        }
        return responseContent;
    }


    @Override
    public ForestResponse createResponse(ForestRequest request, HttpResponse httpResponse) {
        ForestResponse response = new ForestResponse(request, httpResponse);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        response.setStatusCode(statusCode);
        httpResponse.getAllHeaders();
        HttpEntity entity = response.getHttpResponse().getEntity();
        if (entity != null) {
            try {
                String responseText = getString(request.getResponseEncode(), entity);
                response.setContent(responseText);
            } catch (IOException e) {
                throw new ForestRuntimeException(e);
            }
        }
        return response;
    }

}
