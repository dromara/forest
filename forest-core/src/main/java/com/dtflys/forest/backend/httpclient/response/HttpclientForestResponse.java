package com.dtflys.forest.backend.httpclient.response;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-26 15:37
 */
public class HttpclientForestResponse extends ForestResponse {

    private final HttpResponse httpResponse;

    private final HttpEntity entity;

    public HttpclientForestResponse(ForestRequest request, HttpResponse httpResponse) {
        super(request);
        this.httpResponse = httpResponse;
        this.entity = httpResponse.getEntity();
        this.statusCode = httpResponse.getStatusLine().getStatusCode();
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    @Override
    public boolean isReceivedResponseData() {
        return entity != null;
    }

    @Override
    public byte[] getReceivedDataAsByteArray() throws IOException {
        return EntityUtils.toByteArray(entity);
    }

    @Override
    public InputStream getReceivedDataAsInputStream() throws IOException {
        return entity.getContent();
    }
}
