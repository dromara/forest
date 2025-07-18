package com.dtflys.forest.backend.okhttp3.response;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestType;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.GzipUtils;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.StringUtils;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 15:05
 */
public class OkHttp3ForestResponse extends ForestResponse {

    private final Response okResponse;

    private final ResponseBody body;
    
    
    /**
     * 内容字节数组
     */
    private volatile byte[] bytes;

    public OkHttp3ForestResponse(ForestRequest request, Response okResponse, Date requestTime, Date responseTime) {
        this(request, okResponse, requestTime, responseTime, true);
    }

    protected OkHttp3ForestResponse(ForestRequest request, Response okResponse, Date requestTime, Date responseTime, boolean autoClosable) {
        super(request, requestTime, responseTime, autoClosable);
        this.okResponse = okResponse;
        if (okResponse == null) {
            this.body = null;
            this.statusCode = null;
            return;
        }
        final ForestRequestType type = request.type();
        if (ForestRequestType.HEAD != type) {
            this.body = okResponse.body();
        } else {
            this.body = null;
        }
        
        this.statusCode = okResponse.code();
        this.reasonPhrase = okResponse.message();
        setupHeaders();
        setupContentEncoding();
        setupContentTypeAndCharset();
        // 判断是否将Response数据按GZIP来解压
        setupGzip();
        if (autoClosable && !request.isReceiveStream()) {
            readContentAsString();
        }
        if (body == null) {
            return;
        }
        this.contentLength = body.contentLength();
    }

    /**
     * @author designer[19901753334@163.com]
     * @author gongjun[dt_flys@hotmail.com]
     * @date 2021/12/8 23:51
     **/
    private void setupContent() {
        if (request.isReceiveStream()
                || (contentType != null && contentType.canReadAsBinaryStream())) {
            final StringBuilder builder = new StringBuilder();
            builder.append("[stream content-type: ")
                    .append(contentType == null ? "undefined" : contentType.toString());
            if (contentEncoding != null) {
                builder.append("; content-encoding: ")
                        .append(contentEncoding);
            }
            if (charset != null) {
                builder.append("; charset: ")
                        .append(charset);
            }
            builder.append("; length: ")
                    .append(contentLength)
                    .append("]");
            this.content = builder.toString();
        } else {
            this.content = readContentAsString();
        }
    }

    @Override
    public String getContent() {
        if (content == null) {
            setupContent();
        }
        return content;
    }

    /**
     * @author designer[19901753334@163.com]
     * @date 2021/12/8 23:51
     **/
    private void setupGzip() {
        if(this.contentEncoding != null && !request.isDecompressResponseGzipEnabled()){
            isGzip = GzipUtils.isGzip(contentEncoding);
        } else {
            isGzip = true;
        }
    }

    private String readContentAsString() {
        try {
            bytes = getByteArray();
            if (bytes == null) {
                return null;
            }
            return byteToString(bytes);
        } catch (Exception e) {
            throw new ForestRuntimeException(e);
        }
    }

    private void setupHeaders() {
        if (okResponse != null) {
            Headers hs = okResponse.headers();
            Map<String, List<String>> hsMap = hs.toMultimap();
            for (Map.Entry<String, List<String>> entry : hsMap.entrySet()) {
                String name = entry.getKey();
                List<String> values = entry.getValue();
                for (String value : values) {
                    headers.addHeader(name, value);
                }
            }
        }
    }


    /**
     * 从响应体读取并设置 Content-Type 和 Charset
     * 
     * @author designer[19901753334@163.com]
     **/
    protected void setupContentTypeAndCharset() {
        if (body != null) {
            MediaType mediaType = body.contentType();
            if (mediaType != null) {
                String type = mediaType.type();
                String subType = mediaType.subtype();
                this.contentType = new ContentType(type, subType);
                Charset charset = mediaType.charset();
                if (charset != null) {
                    this.charset = charset.name();
                    return;
                }
            }
            setupResponseCharset();
        }
    }


    /**
     * @author designer[19901753334@163.com]
     * @date 2021/12/8 23:51
     **/
    private void setupResponseCharset() {
        if (StringUtils.isNotBlank(request.getResponseEncode())) {
            this.charset = request.getResponseEncode();
        } else if (contentType != null) {
            this.charset = this.contentType.getCharsetName();
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

    /**
     * @author designer[19901753334@163.com]
     * @date 2021/12/8 23:51
     **/
    private void setupContentEncoding() {
        if (StringUtils.isEmpty(this.contentEncoding)) {
            this.contentEncoding = okResponse.header("Content-Encoding");
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
        if (openedStream != null) {
            return openedStream;
        }
        if (bytes != null) {
            openedStream = new ByteArrayInputStream(getByteArray());
        } else {
            openedStream = body.byteStream();
        }
        return openedStream;
    }

    @Override
    public boolean isReceivedResponseData() {
        return body != null;
    }

    @Override
    public byte[] getByteArray() throws Exception {
        if (bytes == null) {
            if (body == null) {
                return null;
            } else {
                try {
                    bytesRead = true;
                    bytes = body.bytes();
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
        if (body != null) {
            try {
                body.close();
            } catch (Throwable th) {
                throw new ForestRuntimeException(th);
            } finally {
                openedStream = null;
                closed = true;
            }
        }
        if (openedStream != null) {
            try {
                openedStream.close();
            } catch (IOException ignored) {
            }
        }
    }
    

}
