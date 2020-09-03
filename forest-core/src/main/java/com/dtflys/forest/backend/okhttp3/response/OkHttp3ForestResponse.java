package com.dtflys.forest.backend.okhttp3.response;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.ByteEncodeUtils;
import com.dtflys.forest.utils.StringUtils;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

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


    public OkHttp3ForestResponse(ForestRequest request, Response okResponse) {
        super(request);
        this.okResponse = okResponse;
        if (okResponse != null) {
            this.body = okResponse.body();
            this.statusCode = okResponse.code();
            String respEncodingFromHeader = okResponse.header("Content-Encoding");
            setupHeaders();
            if (body != null) {
                MediaType mediaType = body.contentType();
                if (mediaType != null) {
                    String type = mediaType.type();
                    String subType = mediaType.subtype();
                    this.contentType = new ContentType(type, subType);
                    Charset charset = mediaType.charset();
                    if (charset != null) {
                        this.contentEncoding = charset.name();
                    }
                }
                if (StringUtils.isEmpty(this.contentEncoding)) {
                    this.contentEncoding = respEncodingFromHeader;
                }
                if (contentType == null || contentType.isEmpty()) {
                    content = null;
                } else if (!request.isDownloadFile() && contentType.canReadAsString()) {
                    try {
                        bytes = body.bytes();
                        String encode = null;
                        if (StringUtils.isNotEmpty(contentEncoding)) {
                            // 默认从Content-Encoding获取字符编码
                            encode = contentEncoding;
                        } else {
                            // Content-Encoding为空的情况下，自动判断字符编码
                            encode = ByteEncodeUtils.getCharsetName(bytes);
                        }
                        if (encode.toUpperCase().startsWith("GB")) {
                            // 返回的GB中文编码会有多种编码类型，这里统一使用GBK编码
                            encode = "GBK";
                        }
                        this.content = IOUtils.toString(bytes, encode);
                    } catch (IOException e) {
                        throw new ForestRuntimeException(e);
                    }
                } else {
                    StringBuilder builder = new StringBuilder();
                    builder.append("[content-type: ")
                            .append(contentType.toString());
                    if (contentEncoding != null) {
                        builder.append("; encoding: ")
                                .append(contentEncoding);
                    }
                    builder.append("; length: ")
                            .append(contentLength)
                            .append("]");
                    this.content = builder.toString();
                }
            }
        } else {
            this.body = null;
            this.statusCode = 404;
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

    public boolean isText() {
        if (contentType == null) {
            return false;
        }
        return false;
    }

    @Override
    public boolean isReceivedResponseData() {
        return body != null;
    }

    @Override
    public byte[] getByteArray() throws Exception {
        if (bytes == null) {
            bytes = body.bytes();
        }
        return bytes;
    }

    @Override
    public InputStream getInputStream() throws Exception {
        if (bytes == null) {
            return body.byteStream();
        }
        return new ByteArrayInputStream(getByteArray());
    }

}
