package com.dtflys.forest.backend.httpclient.response;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.GzipUtils;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-26 15:37
 */
public class HttpclientForestResponse extends ForestResponse {

    private final HttpResponse httpResponse;

    private final HttpEntity entity;

    private byte[] bytes;


    public HttpclientForestResponse(ForestRequest request, HttpResponse httpResponse, HttpEntity entity, Date requestTime, Date responseTime) {
        super(request, requestTime, responseTime);
        this.httpResponse = httpResponse;
        this.entity = entity;
        if (httpResponse != null) {
            setupHeaders();
            StatusLine statusLine = httpResponse.getStatusLine();
            this.statusCode = statusLine.getStatusCode();
            this.reasonPhrase = statusLine.getReasonPhrase();
            if (entity != null) {
                Header type = entity.getContentType();
                if (type != null) {
                    this.contentType = new ContentType(type.getValue());
                }
                //响应消息的编码格式: gzip...
                setupContentEncoding();
                //响应文本的字符串编码
                setupResponseCharset();
                //是否将Response数据按GZIP来解压
                setupGzip();
                setupContent();
                this.contentLength = entity.getContentLength();
            } else {
                this.bytes = new byte[0];
                this.content = "";
            }
        } else {
            this.statusCode = -1;
        }
    }

    private void setupContentEncoding() {
        Header contentEncodingHeader = entity.getContentEncoding();
        if(contentEncodingHeader!= null){
            this.contentEncoding = contentEncodingHeader.getValue();
        }
    }

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

    private void setupContent() {
        if (content == null) {
            if (request.isDownloadFile()
                    || InputStream.class.isAssignableFrom(request.getMethod().getReturnClass())
                    || InputStream.class.isAssignableFrom(ReflectUtils.toClass(request.getLifeCycleHandler().getResultType()))
                    || (contentType != null && contentType.canReadAsBinaryStream())) {
                StringBuilder builder = new StringBuilder();
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
        try {
            InputStream inputStream = entity.getContent();
            if (inputStream == null) {
                return null;
            }
            bytes = IOUtils.toByteArray(inputStream);
            return byteToString(bytes);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public InputStream getInputStream() throws Exception {
        if (bytes != null) {
            return new ByteArrayInputStream(getByteArray());
        }
        return entity.getContent();
    }

    @Override
    public byte[] getByteArray() throws IOException {
        if (bytes == null) {
            if (entity == null) {
                return null;
            } else {
                bytes = EntityUtils.toByteArray(entity);
            }
        }
        return bytes;
    }

}
