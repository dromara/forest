package com.dtflys.forest.backend.okhttp3.body;

import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.utils.ForestProgress;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class OkHttpMultipartBody extends RequestBody {

    private final ForestRequest request;

    private final RequestBody requestBody;

    private final LifeCycleHandler handler;

    private long contentLength = -1;

    private long writtenBytes;

    private BufferedSink bufferedSink;

    private final long progressStep;

    private long currentStep = 0;

    public OkHttpMultipartBody(ForestRequest request, RequestBody requestBody, LifeCycleHandler handler) {
        this.request = request;
        this.requestBody = requestBody;
        this.handler = handler;
        this.progressStep = request.getProgressStep();
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() {
        if (contentLength < 0) {
            try {
                contentLength = requestBody.contentLength();
            } catch (IOException ex) {
                contentLength = -1;
            }
        }
        return contentLength;
    }

    private ForwardingSink getSink(BufferedSink sink) {
        final OkHttpMultipartBody self = this;
        AtomicReference<ForestProgress> progressReference = new AtomicReference<>(null);
        final Boolean[] isBegin = {null};
        ForwardingSink forwardingSink = new ForwardingSink(sink) {
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                if (isBegin[0] == null) {
                    isBegin[0] = true;
                } else {
                    isBegin[0] = false;
                }

                // increment current length of written bytes
                self.writtenBytes += byteCount;
                long totalLength = self.contentLength();
                ForestProgress progress = progressReference.get();
                if (progress == null) {
                    progress = new ForestProgress(self.request, totalLength);
                    progressReference.set(progress);
                }
                progress.setBegin(isBegin[0]);
                progress.setCurrentBytes(self.writtenBytes);
                if (totalLength >= 0) {
                    self.currentStep += byteCount;
                    if (self.writtenBytes == totalLength) {
                        // progress is done
                        progress.setDone(true);
                        self.handler.handleProgress(self.request, progress);
                    } else {
                        while (self.currentStep >= self.progressStep) {
                            self.currentStep = self.currentStep - self.progressStep;
                            progress.setDone(false);
                            // invoke progress listener
                            self.handler.handleProgress(self.request, progress);
                        }
                    }
                }
                super.write(source, byteCount);
            }
        };
        return forwardingSink;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            ForwardingSink forwardingSink = getSink(sink);
            // transfer to buffered sink
            bufferedSink = Okio.buffer(forwardingSink);
        }
        // write to buffered sink
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }
}
