/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jun Gong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dtflys.forest.http;


import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.utils.ByteEncodeUtils;
import com.dtflys.forest.utils.StringUtils;
import com.sun.istack.internal.NotNull;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Forest请求响应类
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.1.0
 */
public abstract class ForestResponse<T> {

    protected ForestRequest request;
    protected volatile Integer statusCode;
    protected volatile String content;
    protected volatile String filename;
    protected volatile ContentType contentType;
    protected volatile String contentEncoding;
    protected volatile long contentLength;
    protected volatile ForestHeaderMap headers = new ForestHeaderMap();
    protected volatile Throwable exception;
    protected volatile T result;

    public ForestResponse(ForestRequest request) {
        this.request = request;
    }

    public ForestRequest getRequest() {
        return request;
    }

    public String getContent() {
        return content;
    }

    public synchronized void setContent(String content) {
        this.content = content;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public ForestResponse<T> setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public long getContentLength() {
        return contentLength;
    }

    public boolean isSuccess() {
        return getException() == null
                && getStatusCode() >= HttpStatus.OK
                && getStatusCode() < HttpStatus.MULTIPLE_CHOICES;
    }


    public boolean isError() {
        return !isSuccess();
    }

    public abstract boolean isReceivedResponseData();

    public abstract byte[] getByteArray() throws Exception;

    public InputStream getInputStream() throws Exception {
        return new ByteArrayInputStream(getByteArray());
    }

    public ForestHeader getHeader(String name) {
        return headers.getHeader(name);
    }

    public List<ForestHeader> getHeaders(String name) {
        return headers.getHeaders(name);
    }

    public String getHeaderValue(String name) {
        return headers.getValue(name);
    }

    public List<String> getHeaderValues(String name) {
        return headers.getValues(name);
    }

    public ForestHeaderMap getHeaders() {
        return headers;
    }

    /**
     * 把字节数组转换成字符串（自动根据字符串编码转换）
     *
     * @param bytes 字节数组
     * @return 字符串
     * @throws IOException 字符串处理异常
     */
    @NotNull
    protected String byteToString(byte[] bytes) throws IOException {
        String encode;
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
        return IOUtils.toString(bytes, encode);
    }
}
