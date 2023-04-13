package org.dromara.forest.backend.httpclient.body;

import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.multipart.ForestMultipart;
import org.dromara.forest.utils.ForestProgress;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.util.Args;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * HttpClient后端通用类型的MultipartBody封装，主要用于文件上传以及上传时的进度监听
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-08-02
 */
public class HttpclientMultipartCommonBody extends AbstractContentBody {

    private final ForestRequest request;

    private final LifeCycleHandler handler;

    private final ForestMultipart multipart;

    private final String filename;

    private long contentLength = -1;

    private long writtenBytes;

    private final long progressStep;

    private long currentStep = 0;


    public HttpclientMultipartCommonBody(ForestRequest request, ForestMultipart multipart, ContentType contentType, String filename, LifeCycleHandler handler) {
        super(contentType);
        this.request = request;
        this.multipart = multipart;
        this.filename = multipart.getOriginalFileName();
        this.handler = handler;
        this.progressStep = request.getProgressStep();
    }

    @Override
    public String getTransferEncoding() {
        return "binary";
    }

    @Override
    public long getContentLength() {
        return multipart.getSize();
    }

    public InputStream getInputStream() {
        return multipart.getInputStream();
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        Args.notNull(out, "Output stream");
        if (contentLength < 0) {
            contentLength = getContentLength();
        }
        InputStream in = getInputStream();
        ForestProgress progress = new ForestProgress(request, contentLength);
        try {
            byte[] tmp = new byte[4096];
            int len;
            progress.setBegin(true);
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
        } catch (Throwable th2) {
            th2.printStackTrace();
        } finally {
            in.close();
        }
    }
}
