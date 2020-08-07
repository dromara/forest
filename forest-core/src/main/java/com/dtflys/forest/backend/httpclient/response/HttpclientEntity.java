package com.dtflys.forest.backend.httpclient.response;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpclientEntity implements HttpEntity {

    private final HttpEntity entity;

    public HttpclientEntity(HttpEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean isRepeatable() {
        return entity.isRepeatable();
    }

    @Override
    public boolean isChunked() {
        return entity.isChunked();
    }

    @Override
    public long getContentLength() {
        return entity.getContentLength();
    }

    @Override
    public Header getContentType() {
        return entity.getContentType();
    }

    @Override
    public Header getContentEncoding() {
        return entity.getContentEncoding();
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        return entity.getContent();
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        entity.writeTo(outputStream);
    }

    @Override
    public boolean isStreaming() {
        return entity.isStreaming();
    }

    @Override
    public void consumeContent() throws IOException {
        entity.consumeContent();
    }
}
