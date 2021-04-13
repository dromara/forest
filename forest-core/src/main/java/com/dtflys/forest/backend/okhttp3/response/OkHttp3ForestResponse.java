package com.dtflys.forest.backend.okhttp3.response;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.StringUtils;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import okio.GzipSource;
import okio.Okio;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 15:05
 */
public class OkHttp3ForestResponse extends ForestResponse {

    private final Response okResponse;

    private final ResponseBody body;

    /**
     * 内容字节数组
     */
    private byte[] bytes;

    public OkHttp3ForestResponse(ForestRequest request, Response okResponse, Date requestTime, Date responseTime) {
        super(request, requestTime, responseTime);
        this.okResponse = okResponse;
        if (okResponse == null) {
            this.body = null;
            this.statusCode = null;
            return;
        }
        String contentEncoding = okResponse.headers().get("Content-Encoding");
        // 如果手动添加Accept-Encoding = gzip。则需要手动处理gzip
        if ("gzip".equals(contentEncoding)) {
            if(okResponse.body() != null){
                String contentType = okResponse.header("Content-Type");
                GzipSource responseBody = new GzipSource(okResponse.body().source());
                Headers strippedHeaders = okResponse.headers().newBuilder().build();
                okResponse = okResponse.newBuilder().headers(strippedHeaders)
                        .body(new RealResponseBody(contentType, -1L, Okio.buffer(responseBody)))
                        .build();
            }
        }
        this.body = okResponse.body();
        this.statusCode = okResponse.code();
        setupHeaders();
        if (body == null) {
            return;
        }
        setupContentTypeAndEncoding();
        if (contentType == null || contentType.isEmpty()) {
            this.content = readContentAsString();
        } else if (!request.isDownloadFile() && contentType.canReadAsString()) {
            this.content = readContentAsString();
        } else if (contentType.canReadAsBinaryStream()) {
            StringBuilder builder = new StringBuilder();
            builder.append("[content-type: ")
                    .append(contentType.toString());
            if (contentEncoding != null) {
                builder.append("; encoding: ")
                        .append(contentEncoding);
            }
            builder.append("; length: ")
                    .append(contentLength)
                    .append("]");
            this.content = builder.toString();
        }
    }

    private String readContentAsString() {
        try {
            bytes = body.bytes();
            if (bytes == null) {
                return null;
            }
            return byteToString(bytes);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    private void setupHeaders() {
        if (okResponse != null) {
            Headers hs = okResponse.headers();
            for (String name : hs.names()) {
                headers.addHeader(name, hs.get(name));
            }
        }
    }

    private void setupContentTypeAndEncoding() {
        MediaType mediaType = body.contentType();
        if (mediaType != null) {
            String type = mediaType.type();
            String subType = mediaType.subtype();
            this.contentType = new ContentType(type, subType);
            Charset charset = mediaType.charset();
            if (charset != null) {
                this.contentEncoding = charset.name();
            }
        }
        if (StringUtils.isEmpty(this.contentEncoding)) {
            this.contentEncoding = okResponse.header("Content-Encoding");
        }
    }

    @Override
    public boolean isReceivedResponseData() {
        return body != null;
    }

    @Override
    public byte[] getByteArray() throws Exception {
        if (bytes == null) {
            if (body == null) {
                return null;
            } else {
                bytes = body.bytes();
            }
        }
        return bytes;
    }

}
