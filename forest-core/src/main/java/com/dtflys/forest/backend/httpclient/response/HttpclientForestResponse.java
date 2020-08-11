package com.dtflys.forest.backend.httpclient.response;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
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
                } else {
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
                return null;
            } else if (!request.isDownloadFile() && contentType.canReadAsString()) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    content = IOUtils.toString(inputStream, contentEncoding);
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
                return builder.toString();
            }
        }
        return content;
    }

    @Override
    public byte[] getByteArray() throws IOException {
        return EntityUtils.toByteArray(entity);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return entity.getContent();
    }
}
