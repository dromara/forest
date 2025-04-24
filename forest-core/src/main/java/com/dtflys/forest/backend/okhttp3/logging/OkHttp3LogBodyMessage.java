package com.dtflys.forest.backend.okhttp3.logging;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.exceptions.ForestRuntimeException;
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
import java.nio.charset.StandardCharsets;
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
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Sink sink = Okio.sink(out);
        final BufferedSink bufferedSink = Okio.buffer(sink);
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
        final InputStream inputStream = new ByteArrayInputStream(out.toByteArray());
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final StringBuilder builder = new StringBuilder();
        String line;
        try {
            final List<String> lines = new LinkedList<>();
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            for (int i = 0, len = lines.size(); i < len; i++) {
                builder.append(lines.get(i));
                if (i < len - 1) {
                    builder.append("\\n");
                }
            }
            return builder.toString();
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
        final MediaType mediaType = requestBody.contentType();
        if (mediaType == null) {
            return getLogContentForStringBody(this.requestBody);
        }
        final ContentType contentType = new ContentType(mediaType.toString(), StandardCharsets.UTF_8);
        if (contentType.isMultipart()) {
            final StringBuilder builder = new StringBuilder();
            try {
                final MultipartBody multipartBody = (MultipartBody) requestBody;
                final String boundary = multipartBody.boundary();
                Long contentLength = null;
                try {
                    contentLength = multipartBody.contentLength();
                } catch (IOException e) {
                }
                builder.append("[")
                        .append("boundary=")
                        .append(boundary);
                if (contentLength != null) {
                    builder.append("; length=").append(contentLength);
                }
                builder.append("] parts:");
                final List<MultipartBody.Part> parts = multipartBody.parts();
                for (final MultipartBody.Part part : parts) {
                    final RequestBody partBody = part.body();
                    final List<String> disposition = part.headers().values("Content-Disposition");
                    builder.append("\n             -- [")
                            .append(disposition.get(0));
                    final MediaType partMediaType = partBody.contentType();
                    if (partMediaType == null) {
                        builder.append("; content-type=\"")
                                .append(partBody.contentType())
                                .append("\"");
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
                        builder.append("; content-type=\"")
                                .append(partBody.contentType())
                                .append("\"");
                        builder.append("]");
                    }
                }
            } catch (Throwable th) {
            }
            return builder.toString();
        } else if (contentType.isBinary()) {
            try {
                return "[Binary length=" + requestBody.contentLength() + "]";
            } catch (IOException e) {
                throw new ForestRuntimeException(e);
            }
        }
        return getLogContentForStringBody(this.requestBody);
    }
}
