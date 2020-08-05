package com.dtflys.forest.backend.okhttp3.response;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.StringUtils;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

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
        if (okResponse != null) {
            this.body = okResponse.body();
            this.statusCode = okResponse.code();
            if (body != null) {
                MediaType mediaType = body.contentType();
                if (mediaType != null) {
                    this.contentType = mediaType.type();
                    Charset charset = mediaType.charset();
                    if (charset != null) {
                        this.contentEncoding = charset.name();
                    }
                }
                if (StringUtils.isEmpty(contentType)) {
                    content = null;
                } else if (contentType.startsWith("application")
                        || contentType.startsWith("text")) {
                    try {
                        this.content = body.string();
                    } catch (IOException e) {
                        throw new ForestRuntimeException(e);
                    }
                } else {
                    StringBuilder builder = new StringBuilder();
                    builder.append("[content-type: ")
                            .append(contentType);
                    if (contentEncoding != null) {
                        builder.append("; encoding: ")
                                .append(contentEncoding);
                    }
                    builder.append("; length: ")
                            .append(contentLength)
                            .append("]");
                    this.contentType = builder.toString();
                }
            }
        } else {
            this.body = null;
            this.statusCode = 404;
        }
    }

    @Override
    public boolean isReceivedResponseData() {
        return body != null;
    }

    @Override
    public byte[] getByteArray() throws Exception {
        return body.bytes();
    }

    @Override
    public InputStream getInputStream() throws Exception {
        return body.byteStream();
    }
}
