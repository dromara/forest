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

import java.io.InputStream;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-03-25 16:33
 */
public abstract class ForestResponse<T> {

    protected ForestRequest request;
    protected volatile Integer statusCode;
    protected volatile String content;
    protected volatile String filename;
    protected volatile ContentType contentType;
    protected volatile String contentEncoding;
    protected volatile long contentLength;
    protected volatile T result;

    public ForestResponse(ForestRequest request) {
        this.request = request;
    }

    public ForestRequest getRequest() {
        return request;
    }

    public synchronized void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
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
        return getStatusCode() >= HttpStatus.OK && getStatusCode() < HttpStatus.MULTIPLE_CHOICES;
    }


    public boolean isError() {
        return !isSuccess();
    }

    public abstract boolean isReceivedResponseData();

    public abstract byte[] getByteArray() throws Exception;

    public abstract InputStream getInputStream() throws Exception;

}
