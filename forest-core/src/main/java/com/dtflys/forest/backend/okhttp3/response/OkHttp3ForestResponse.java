package com.dtflys.forest.backend.okhttp3.response;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestHttpInputStream;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.GzipUtils;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.StringUtils;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
    private byte[] rawBytes;

    public OkHttp3ForestResponse(ForestRequest request, Response okResponse, Date requestTime, Date responseTime) {
        super(request, requestTime, responseTime);
        this.okResponse = okResponse;
        if (okResponse == null) {
            this.body = null;
            this.statusCode = null;
            return;
        }

        this.body = okResponse.body();
        this.statusCode = okResponse.code();
        this.reasonPhrase = okResponse.message();
        // 判断是否将Response数据按GZIP来解压
//        setupGzip();
        if (body == null) {
            return;
        }
        init();
//        setupContentTypeAndCharset();
//        setupContent();
    }


    private void init() {
        setupContentTypeAndCharset();
        if (request.isDownloadFile()
                || InputStream.class.isAssignableFrom(request.getResultClass())
                || InputStream.class.isAssignableFrom(ReflectUtils.toClass(request.getLifeCycleHandler().getResultType()))
                || (contentType != null && contentType.canReadAsBinaryStream())) {
        } else {
            try {
                this.rawBytes = getRawBytes();
            } catch (Exception e) {
                throw new ForestRuntimeException(e);
            }
        }
    }

    /**
     * @author designer[19901753334@163.com]
     * @author gongjun[dt_flys@hotmail.com]
     * @date 2021/12/8 23:51
     **/
    private void setupContent() {
        if (request.isDownloadFile()
                || InputStream.class.isAssignableFrom(request.getResultClass())
                || InputStream.class.isAssignableFrom(ReflectUtils.toClass(request.getLifeCycleHandler().getResultType()))
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
            rawBytes = getRawBytes();
            if (rawBytes == null) {
                return null;
            }
            return byteToString(rawBytes);
        } catch (Exception e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    protected void setupHeaders() {
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
     * @author designer[19901753334@163.com]
     * @date 2021/12/8 23:51
     **/
    private void setupContentTypeAndCharset() {
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

    @Override
    public String getContentEncoding() {
        if (this.contentEncoding == null) {
            this.contentEncoding = okResponse.header("Content-Encoding");
        }
        return this.contentEncoding;
    }


    @Override
    public InputStream getInputStream() throws Exception {
        return new ForestHttpInputStream(this);
    }

    @Override
    public boolean isReceivedResponseData() {
        return body != null;
    }

    @Override
    public byte[] getRawBytes() throws Exception {
        if (rawBytes == null) {
            if (body == null) {
                return null;
            } else {
                try {
                    rawBytes = body.bytes();
                } finally {
                    close();
                }
            }
        }
        return rawBytes;
    }

    @Override
    public InputStream getRawInputStream() throws Exception {
        if (rawBytes != null) {
            return new ByteArrayInputStream(rawBytes);
        }
        return body.byteStream();
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
                closed = true;
            }
        }
    }
}
