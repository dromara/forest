package com.dtflys.forest.backend.httpclient.response;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestType;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.GzipUtils;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.StringUtils;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.util.EntityUtils;

import javax.mail.UIDFolder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.PhantomReference;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-26 15:37
 */
public class HttpclientForestResponse extends ForestResponse {

    private final HttpResponse httpResponse;

    private volatile HttpEntity entity;

    private volatile byte[] bytes;
    

    public HttpclientForestResponse(ForestRequest request, HttpResponse httpResponse, HttpEntity entity, Date requestTime, Date responseTime) {
        this(request, httpResponse, entity, requestTime, responseTime, true);
    }
    
    protected HttpclientForestResponse(ForestRequest request, HttpResponse httpResponse, HttpEntity entity, Date requestTime, Date responseTime, boolean autoClosable) {
        super(request, requestTime, responseTime, autoClosable);
        this.httpResponse = httpResponse;
        this.entity = entity;
        if (httpResponse != null) {
            setupHeaders();
            StatusLine statusLine = httpResponse.getStatusLine();
            this.statusCode = statusLine.getStatusCode();
            this.reasonPhrase = statusLine.getReasonPhrase();
            if (entity != null) {
                final Header type = entity.getContentType();
                if (type != null) {
                    this.contentType = new ContentType(type.getValue(), StandardCharsets.UTF_8);
                }
                //是否将Response数据按GZIP来解压
                setupGzip();
                //响应消息的编码格式: gzip...
                setupContentEncoding();
                //响应文本的字符串编码
                setupResponseCharset();
                if (autoClosable && !request.isReceiveStream()) {
                    readContentAsString();
                }
            }
        } else {
            this.statusCode = -1;
        }
    }

    @Override
    public String getContent() {
        if (content == null) {
            synchronized (this) {
                if (content == null) {
                    if (entity != null) {
                        setupContent();
                        this.contentLength = entity.getContentLength();
                    } else {
                        this.content = "";
                    }
                }
            }
        }
        return content;

    }

    private void setupContentEncoding() {
        final Header contentEncodingHeader = entity.getContentEncoding();
        if (contentEncodingHeader!= null) {
            this.contentEncoding = contentEncodingHeader.getValue();
        }
    }

    private void setupResponseCharset() {
        if (StringUtils.isNotBlank(request.getResponseEncode())) {
            this.charset = request.getResponseEncode();
        } else if (contentType != null) {
            this.charset = this.contentType.getCharset().name();
        } else {
            if (this.contentEncoding != null) {
                try {
                    Charset.forName(this.contentEncoding);
                    this.charset = this.contentEncoding;
                } catch (Throwable ignored) {
                }
            }
        }
    }

    private void setupContent() {
        if (content == null) {
            if (request.isReceiveStream()
                    || (contentType != null && contentType.canReadAsBinaryStream())) {
                final StringBuilder builder = new StringBuilder();
                builder.append("[stream content-type: ")
                        .append(contentType == null ? "undefined" : contentType);
                if (contentEncoding != null) {
                    builder.append("; encoding: ")
                            .append(contentEncoding);
                }
                builder.append("; length: ")
                        .append(contentLength)
                        .append("]");
                this.content = builder.toString();
            } else {
                content = readContentAsString();
            }
        }
    }

    /**
     * @author designer[19901753334@163.com]
     * @date 2021/12/8 23:51
     **/
    private void setupGzip() {
        //响应消息的编码格式: gzip...
        if(this.contentEncoding != null && !request.isDecompressResponseGzipEnabled()){
            isGzip = GzipUtils.isGzip(contentEncoding);
        } else {
            isGzip = true;
        }
    }

    /**
     * @author designer[19901753334@163.com]
     * @date 2021/12/8 23:51
     **/
    private void setupHeaders() {
        if (httpResponse != null) {
            HeaderIterator it = httpResponse.headerIterator();
            if (it != null) {
                for (; it.hasNext(); ) {
                    Header header = it.nextHeader();
                    headers.addHeader(header.getName(), header.getValue());
                }
            }
        }
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    @Override
    public boolean isReceivedResponseData() {
        return entity != null || bytes != null;
    }

    private String readContentAsString() {
        try  {
            bytes = getByteArray();
            return byteToString(bytes);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        } finally {
            closed = true;
        }
    }

    @Override
    public InputStream getInputStream() throws Exception {
        if (openedStream != null) {
            if (openedStream.available() > 0) {
                return openedStream;
            } else {
                openedStream = null;
            }
        }
        if (bytes != null) {
            openedStream = new ByteArrayInputStream(getByteArray());
        } else {
            openedStream = entity.getContent();
        }
        return openedStream;
    }

    @Override
    public byte[] getByteArray() throws IOException {
        if (bytes == null) {
            if (entity == null) {
                return null;
            } else {
                try {
                    bytesRead = true;
                    bytes = EntityUtils.toByteArray(entity);
                } finally {
                    close();
                }
            }
        }
        return bytes;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        if (entity != null) {
            try {
                EntityUtils.consume(entity);
                entity = null;
            } catch (IOException ignored) {
            }
        }
        try {
            if (httpResponse instanceof CloseableHttpResponse) {
                ((CloseableHttpResponse) httpResponse).close();
            }
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        } finally {
            closed = true;
        }
        if (openedStream != null) {
            try {
                openedStream.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public boolean isTimeout() {
        if (noException()) {
            return false;
        }
        return exception instanceof SocketTimeoutException ||
                exception instanceof ConnectionPoolTimeoutException ||
                exception instanceof ConnectTimeoutException;
    }
}
