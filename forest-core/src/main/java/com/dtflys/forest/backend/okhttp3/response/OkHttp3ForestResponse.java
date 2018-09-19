package com.dtflys.forest.backend.okhttp3.response;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 15:05
 */
public class OkHttp3ForestResponse extends ForestResponse {

    private final Response okResponse;

    private final ResponseBody body;

    public OkHttp3ForestResponse(ForestRequest request, Response okResponse) {
        super(request);
        this.okResponse = okResponse;
        this.body = okResponse.body();
        this.statusCode = okResponse.code();
        if (body != null) {
            try {
                this.content = body.string();
            } catch (IOException e) {
                throw new ForestRuntimeException(e);
            }
        }
    }

    @Override
    public boolean isReceivedResponseData() {
        return body != null;
    }

    @Override
    public byte[] getReceivedDataAsByteArray() throws Exception {
        return body.bytes();
    }

    @Override
    public InputStream getReceivedDataAsInputStream() throws Exception {
        return body.byteStream();
    }
}
