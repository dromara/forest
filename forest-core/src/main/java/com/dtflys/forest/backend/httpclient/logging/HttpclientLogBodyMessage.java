package com.dtflys.forest.backend.httpclient.logging;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.backend.httpclient.body.HttpclientMultipartFileBody;
import com.dtflys.forest.logging.LogBodyMessage;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MinimalField;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;

import javax.xml.bind.helpers.AbstractUnmarshallerImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Httpclient后端的请求头日志消息
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-10-16 14:22
 */
public class HttpclientLogBodyMessage implements LogBodyMessage {

    private final HttpEntity entity;

    public HttpclientLogBodyMessage(HttpEntity entity) {
        this.entity = entity;
    }

    private String getLogContentFormBufferedReader(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;
        String body;
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
    }

    private String getLogContentForStringBody(HttpEntity entity) {
        InputStream in = null;
        try {
            in = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            return getLogContentFormBufferedReader(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public String getBodyString() {
        if (entity == null) {
            return null;
        }
        Header contentTypeHeader = entity.getContentType();
        ContentType contentType = new ContentType(contentTypeHeader.getValue());
        if (contentType.isMultipart()) {
            Class[] paramTypes = new Class[0];
            Object[] args = new Object[0];
            List<FormBodyPart> parts = null;
            try {
                Method getMultipartMethod = entity.getClass().getDeclaredMethod("getMultipart", paramTypes);
                getMultipartMethod.setAccessible(true);
                Object multipart = getMultipartMethod.invoke(entity, args);
                if (multipart != null) {
                    Method getBodyPartsMethod = multipart.getClass().getDeclaredMethod("getBodyParts", paramTypes);
                    getBodyPartsMethod.setAccessible(true);
                    parts = (List<FormBodyPart>) getBodyPartsMethod.invoke(multipart, args);
                }
            } catch (NoSuchMethodException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            Long contentLength = null;
            try {
                contentLength = entity.getContentLength();
            } catch (Throwable th) {
            }

            if (parts == null) {
                String result = "[" + entity.getContentType().getValue();
                if (contentLength != null) {
                    result += "; length=" + contentLength;
                }
                return result + "]";
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("[")
                        .append(entity.getContentType().getValue());
                if (contentLength != null) {
                    builder.append("; length=").append(contentLength);
                }
                builder.append("] parts:");
                for (FormBodyPart part : parts) {
                    ContentBody partBody = part.getBody();
                    MinimalField disposition = part.getHeader().getField("Content-Disposition");
                    builder.append("\n             -- [")
                            .append(disposition.getBody());
                    if (partBody instanceof StringBody) {
                        Reader reader = ((StringBody) partBody).getReader();
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        String value = null;
                        try {
                            value = getLogContentFormBufferedReader(bufferedReader);
                        } catch (IOException e) {
                        }
                        builder.append("; content-type=\"")
                                .append(((StringBody) partBody).getContentType())
                                .append("\"");
                        builder.append("; value=\"")
                                .append(value)
                                .append("\"]");
                    } else {
                        Long length = null;
                        length = partBody.getContentLength();
                        if (length != null) {
                            builder.append("; length=").append(length);
                        }
                        if (partBody instanceof HttpclientMultipartFileBody) {
                            builder.append("; content-type=\"")
                                    .append(((HttpclientMultipartFileBody) partBody).getContentType())
                                    .append("\"");
                        }
                        builder.append("]");
                    }
                }
                return builder.toString();
            }
        } else if (contentType.isBinary()) {
            return "[Binary length=" + entity.getContentLength() + "]";
        }
        return getLogContentForStringBody(entity);
    }
}
