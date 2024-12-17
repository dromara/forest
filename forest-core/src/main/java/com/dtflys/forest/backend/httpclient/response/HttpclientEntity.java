package com.dtflys.forest.backend.httpclient.response;

import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.utils.ForestProgress;
import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.*;

public class HttpclientEntity implements HttpEntity {

    private final ForestRequest request;

    private final HttpEntity entity;

    private final LifeCycleHandler handler;

    private long contentLength = -1;

    private long readBytes;

    private final long progressStep;

    private long currentStep = 0;


    public HttpclientEntity(ForestRequest request, HttpEntity entity, LifeCycleHandler handler) {
        this.request = request;
        this.entity = entity;
        this.handler = handler;
        this.progressStep = request.getProgressStep();
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
        if (isStreaming()) {
            final InputStream in = entity.getContent();
            if (request.isSSE()) {
                return in;
            }
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            if (contentLength < 0) {
                contentLength = getContentLength();
            }
            if (contentLength < 0) {
                return in;
            }
            final ForestProgress progress = new ForestProgress(request, contentLength);
            try {
                final byte[] tmp = new byte[4096];
                progress.setBegin(true);
                int len;
                while((len = in.read(tmp)) != -1) {
                    // increment current length of written bytes
                    readBytes += len;
                    progress.setCurrentBytes(readBytes);
                    if (contentLength >= 0) {
                        currentStep += len;
                        if (readBytes == contentLength) {
                            // progress is done
                            progress.setDone(true);
                            handler.handleProgress(request, progress);
                        } else {
                            while (currentStep >= progressStep) {
                                currentStep = currentStep - progressStep;
                                progress.setDone(false);
                                // invoke progress listener
                                handler.handleProgress(request, progress);

                            }
                        }
                    }
                    progress.setBegin(false);
                    out.write(tmp, 0, len);
                }
                out.flush();
            } finally {
                in.close();
            }
            return new ByteArrayInputStream(out.toByteArray());
        }
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
