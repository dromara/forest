package org.dromara.forest.backend.okhttp3.response;

import org.dromara.forest.backend.ContentType;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.utils.GzipUtil;
import org.dromara.forest.utils.ReflectUtil;
import org.dromara.forest.utils.StringUtil;
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
     * @author gongjun[dt_flys@hotmail.com]
     * @date 2021/12/8 23:51
     **/
    private void setupContent() {
        if (request.isDownloadFile()
                || InputStream.class.isAssignableFrom(request.getMethod().getReturnClass())
                || InputStream.class.isAssignableFrom(ReflectUtil.toClass(request.getLifeCycleHandler().getResultType()))
                || (contentType != null && contentType.canReadAsBinaryStream())) {
            StringBuilder builder = new StringBuilder();
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
            isGzip = GzipUtil.isGzip(contentEncoding);
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
        if (StringUtil.isNotBlank(request.getResponseEncode())) {
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
        if (StringUtil.isEmpty(this.contentEncoding)) {
            this.contentEncoding = okResponse.header("Content-Encoding");
        }
    }

    @Override
    public InputStream getInputStream() throws Exception {
        if (bytes != null) {
            return new ByteArrayInputStream(getByteArray());
        }
        return body.byteStream();
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
                closed = true;
            }
        }
    }
}
