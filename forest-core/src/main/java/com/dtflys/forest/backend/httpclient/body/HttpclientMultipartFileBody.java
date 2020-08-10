package com.dtflys.forest.backend.httpclient.body;

import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.utils.ForestProgress;
import okio.BufferedSink;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.Args;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * HttpClient后端的的对于文件类型的MultipartBody封装，主要用于文件上传以及上传时的进度监听
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-08-02
 */
public class HttpclientMultipartFileBody extends FileBody {

    private final ForestRequest request;

    private final LifeCycleHandler handler;

    private final File file;

    private long contentLength = -1;

    private long writtenBytes;

    private BufferedSink bufferedSink;

    private final long progressStep;

    private long currentStep = 0;


    public HttpclientMultipartFileBody(ForestRequest request, File file, ContentType contentType, String filename, LifeCycleHandler handler) {
        super(file, contentType, filename);
        this.request = request;
        this.file = file;
        this.handler = handler;
        this.progressStep = request.getProgressStep();
    }


    @Override
    public void writeTo(OutputStream out) throws IOException {
        Args.notNull(out, "Output stream");
        FileInputStream in = new FileInputStream(this.file);
        if (contentLength < 0) {
            contentLength = getContentLength();
        }
        ForestProgress progress = new ForestProgress(request, contentLength);
        try {
            byte[] tmp = new byte[4096];
            progress.setBegin(true);
            int len;
            while((len = in.read(tmp)) != -1) {
                // increment current length of written bytes
                writtenBytes += len;
                progress.setCurrentBytes(writtenBytes);
                if (contentLength >= 0) {
                    currentStep += len;
                    if (writtenBytes == contentLength) {
                        // progress is done
                        progress.setDone(true);
                        handler.handleProgress(request, progress);
                        progress.setBegin(false);
                    } else {
                        while (currentStep >= progressStep) {
                            currentStep = currentStep - progressStep;
                            progress.setDone(false);
                            // invoke progress listener
                            handler.handleProgress(request, progress);
                            progress.setBegin(false);
                        }
                    }
                }
                out.write(tmp, 0, len);
            }
            out.flush();
        } finally {
            in.close();
        }

    }
}
