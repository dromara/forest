package com.dtflys.forest.backend.httpclient.response;

import com.dtflys.forest.exceptions.ForestRuntimeException;
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

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-12 17:07
 */
public class HttpclientForestResponseFactory implements ForestResponseFactory<HttpResponse> {

    private String responseContent;

    private volatile ForestResponse resultResponse;


    private String getStringContent(String encode, HttpEntity entity) throws IOException {
        if (responseContent == null) {
            InputStream inputStream = entity.getContent();
            responseContent = IOUtils.toString(inputStream, encode);
        }
        return responseContent;
    }


    @Override
    public synchronized ForestResponse createResponse(ForestRequest request, HttpResponse httpResponse) {
        if (resultResponse != null) {
            return resultResponse;
        }
        if (httpResponse == null) {
            httpResponse = new BasicHttpResponse(
                    new BasicStatusLine(
                            new ProtocolVersion("1.1", 1, 1), 404, ""));
        }
        HttpclientForestResponse response = new HttpclientForestResponse(request, httpResponse);
//        int statusCode = httpResponse.getStatusLine().getStatusCode();
//        response.setStatusCode(statusCode);
//        httpResponse.getAllHeaders();
//        HttpEntity entity = response.getHttpResponse().getEntity();
//        if (entity != null) {
//            try {
//                String responseText = getStringContent(request.getResponseEncode(), entity);
//                response.setContent(responseText);
//            } catch (IOException e) {
//                throw new ForestRuntimeException(e);
//            }
//        }
        this.resultResponse = response;
        return response;
    }

}
