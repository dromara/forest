package com.dtflys.forest.backend.okhttp3.response;

import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.utils.ForestProgress;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class OkHttpResponseBody extends ResponseBody {

    private final ForestRequest request;
    private final ResponseBody responseBody;
    private final LifeCycleHandler handler;
    private BufferedSource bufferedSource;
    private long contentLength = -1;
    private final long progressStep;
    private long currentStep = 0;

    public OkHttpResponseBody(ForestRequest request, ResponseBody responseBody, LifeCycleHandler handler) {
        this.request = request;
        this.responseBody = responseBody;
        this.handler = handler;
        this.progressStep = request.getProgressStep();
    }


    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        if (contentLength < 0) {
            contentLength = responseBody.contentLength();
        }
        return contentLength;
    }


    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    @Override
    public void close() {
        System.out.println("[Forest Debug] Response Body Close!");
        super.close();
    }

    private Source source(Source source) {

        return new ForwardingSource(source) {
            long readBytes = 0L;
            AtomicReference<ForestProgress> progressReference = new AtomicReference<>(null);
            final Boolean[] isBegin = {null};

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                if (isBegin[0] == null) {
                    isBegin[0] = true;
                } else {
                    isBegin[0] = false;
                }

                long totalLength = contentLength();
                long bytesRead = super.read(sink, byteCount);
                ForestProgress progress = progressReference.get();
                if (progress == null) {
                    progress = new ForestProgress(request, totalLength);
                    progressReference.set(progress);
                }
                progress.setBegin(isBegin[0]);
                if (progress.isDone()) {
                    return bytesRead;
                }
                if (totalLength >= 0) {
                    long currReadBytes = bytesRead != -1 ? bytesRead : 0;
                    readBytes += currReadBytes;
                    progress.setCurrentBytes(readBytes);
                    currentStep += currReadBytes;
                    if (readBytes == totalLength) {
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
                return bytesRead;
            }
        };
    }
}
