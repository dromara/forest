package com.dtflys.forest.backend.okhttp3.body;

import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.utils.ForestProgress;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

import java.io.IOException;

public class OkHttpMultipartBody extends RequestBody {

    private final ForestRequest request;

    private final MultipartBody multipartBody;

    private final OnProgress onProgress;

    private long writtenBytes;

    private BufferedSink bufferedSink;

    public OkHttpMultipartBody(ForestRequest request, MultipartBody multipartBody, OnProgress onProgress) {
        this.request = request;
        this.multipartBody = multipartBody;
        this.onProgress = onProgress;
    }

    @Override
    public MediaType contentType() {
        return multipartBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return multipartBody.contentLength();
    }

    private ForwardingSink getSink(BufferedSink sink) {
        ForwardingSink forwardingSink = new ForwardingSink(sink) {
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                // increment current length of written bytes
                writtenBytes += byteCount;
                long totalLength = contentLength();
                // invoke callback function
                if (onProgress != null) {
                    ForestProgress progress = new ForestProgress(request, writtenBytes, totalLength, writtenBytes == totalLength);
                    onProgress.onProgress(progress);
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
        multipartBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }
}
