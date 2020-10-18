package com.dtflys.forest.backend.okhttp3.logging;

import com.dtflys.forest.logging.LogBodyMessage;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * OkHttp3后端的请求头日志消息
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-10-16 14:22
 */
public class OkHttp3LogBodyMessage implements LogBodyMessage {

    private final RequestBody requestBody;

    public OkHttp3LogBodyMessage(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    private String getLogContentForStringBody(RequestBody requestBody) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Sink sink = Okio.sink(out);
        BufferedSink bufferedSink = Okio.buffer(sink);
        try {
            requestBody.writeTo(bufferedSink);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedSink.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        InputStream inputStream = new ByteArrayInputStream(out.toByteArray());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        String body;
        try {
            List<String> lines = new LinkedList<>();
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            for (int i = 0, len = lines.size(); i < len; i++) {
                builder.append(lines.get(i));
                if (i < len - 1) {
                    builder.append("\\n");
                }
            }
            body = builder.toString();
            return body;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    @Override
    public String getBodyString() {
        if (requestBody == null) {
            return null;
        }
        if (requestBody instanceof MultipartBody) {
            MultipartBody multipartBody = (MultipartBody) requestBody;
            String boundary = multipartBody.boundary();
            Long contentLength = null;
            try {
                contentLength = multipartBody.contentLength();
            } catch (IOException e) {
            }
            StringBuilder builder = new StringBuilder();
            builder.append("[")
                    .append("boundary=")
                    .append(boundary);
            if (contentLength != null) {
                builder.append("; length=").append(contentLength);
            }
            builder.append("] parts:");
            List<MultipartBody.Part> parts = multipartBody.parts();
            for (MultipartBody.Part part : parts) {
                RequestBody partBody = part.body();
                List<String> disposition = part.headers().values("Content-Disposition");
                builder.append("\n             -- [")
                        .append(disposition.get(0));
                MediaType mediaType = partBody.contentType();
                if (mediaType == null) {
                    builder.append("; value=\"")
                            .append(getLogContentForStringBody(partBody))
                            .append("\"]");
                } else {
                    Long length = null;
                    try {
                        length = partBody.contentLength();
                    } catch (IOException e) {
                    }
                    if (length != null) {
                        builder.append("; length=").append(length);
                    }
                    builder.append("]");
                }
            }

            return builder.toString();
        }

        return getLogContentForStringBody(this.requestBody);
    }
}
