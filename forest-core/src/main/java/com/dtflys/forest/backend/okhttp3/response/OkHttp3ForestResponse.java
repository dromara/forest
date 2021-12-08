package com.dtflys.forest.backend.okhttp3.response;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.GzipUtils;
import com.dtflys.forest.utils.StringUtils;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;

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
    private byte[] bytes;

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
        setupHeaders();
        setupContentEncoding();
        // 判断是否将Response数据按GZIP来解压
        setupGzip();
        if (body == null) {
            return;
        }
        setupContentTypeAndCharset();
        setupContent();
    }

    /**
     * @author designer[19901753334@163.com]
     * @date 2021/12/8 23:51
     **/
    private void setupContent() {
        if (contentType == null || contentType.isEmpty()) {
            this.content = readContentAsString();
        } else if (!request.isDownloadFile() && contentType.canReadAsString()) {
//            this.content = readContentAsString();
        } else if (contentType.canReadAsBinaryStream()) {
            StringBuilder builder = new StringBuilder();
            builder.append("[content-type: ")
                    .append(contentType.toString());
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
            bytes = body.bytes();
            if (bytes == null) {
                return null;
            }
            return byteToString(bytes);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    private void setupHeaders() {
        if (okResponse != null) {
            Headers hs = okResponse.headers();
            for (String name : hs.names()) {
                headers.addHeader(name, hs.get(name));
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
            this.charset = this.contentType.getCharset();
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
        if (this.contentLength > Integer.MAX_VALUE) {
            return body.byteStream();
        }
        return new ByteArrayInputStream(getByteArray());
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
                bytes = body.bytes();
            }
        }
        return bytes;
    }

}
