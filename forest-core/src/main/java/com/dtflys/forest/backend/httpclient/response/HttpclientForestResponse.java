package com.dtflys.forest.backend.httpclient.response;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.ByteEncodeUtils;
import com.dtflys.forest.utils.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-26 15:37
 */
public class HttpclientForestResponse extends ForestResponse {

    private final HttpResponse httpResponse;

    private final HttpEntity entity;

    private byte[] bytes;


    public HttpclientForestResponse(ForestRequest request, HttpResponse httpResponse, HttpEntity entity) {
        super(request);
        this.httpResponse = httpResponse;
        this.entity = entity;
        if (httpResponse != null) {
            setupHeaders();
            this.statusCode = httpResponse.getStatusLine().getStatusCode();
            if (entity != null) {
                Header type = entity.getContentType();
                if (type != null) {
                    this.contentType = new ContentType(type.getValue());
                }
                this.contentLength = entity.getContentLength();
                Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    this.contentEncoding = encoding.getValue();
                } else if (contentType != null) {
                    this.contentEncoding = contentType.getCharset();
                }
                this.content = buildContent();
            }
        } else {
            this.statusCode = 404;
        }
    }

    private void setupHeaders() {
        if (httpResponse != null) {
            HeaderIterator it = httpResponse.headerIterator();
            if (it != null) {
                for (; it.hasNext(); ) {
                    Header header = it.nextHeader();
                    headers.addHeader(header.getName(), header.getValue());
                }
            }
        }
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    @Override
    public boolean isReceivedResponseData() {
        return entity != null;
    }

    private String buildContent() {
        if (content == null) {
            if (contentType == null || contentType.isEmpty()) {
                content = readContentAsString();
            } else if (!request.isDownloadFile() && contentType.canReadAsString()) {
                content = readContentAsString();
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
                return builder.toString();
            }
        }
        return content;
    }

    private String readContentAsString() {
        try {
            InputStream inputStream = entity.getContent();
            if (inputStream == null) {
                return null;
            }
            bytes = IOUtils.toByteArray(inputStream);
            String encode = null;
            if (StringUtils.isNotEmpty(contentEncoding)) {
                // 默认从Content-Encoding获取字符编码
                encode = contentEncoding;
            } else {
                // Content-Encoding为空的情况下，自动判断字符编码
                encode = ByteEncodeUtils.getCharsetName(bytes);
            }
            if (encode.toUpperCase().startsWith("GB")) {
                // 返回的GB中文编码会有多种编码类型，这里统一使用GBK编码
                encode = "GBK";
            }
            return IOUtils.toString(bytes, encode);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public byte[] getByteArray() throws IOException {
        if (bytes == null) {
            bytes = EntityUtils.toByteArray(entity);
        }
        return bytes;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (bytes == null) {
            return entity.getContent();
        }
        return new ByteArrayInputStream(bytes);
    }
}
