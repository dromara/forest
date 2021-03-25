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
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.ByteEncodeUtils;
import com.dtflys.forest.utils.StringUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Forest请求响应类
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.1.0
 */
public abstract class ForestResponse<T> {

    /**
     * 请求对象
     */
    protected ForestRequest request;

    /**
     * 请求开始时间
     */
    protected Date requestTime;

    /**
     * 响应接受时间
     */
    protected Date responseTime;

    /**
     * 是否已经打印过响应日志
     */
    protected volatile boolean logged = false;

    /**
     * 网络状态码
     */
    protected volatile Integer statusCode;

    /**
     * 响应内容文本（不包括二进制数据内容的文本）
     */
    protected volatile String content;

    /**
     * 响应内容的数据类型
     */
    protected volatile ContentType contentType;

    /**
     * 响应内容的数据编码
     */
    protected volatile String contentEncoding;

    /**
     * 请求响应内容的数据长度
     */
    protected volatile long contentLength;

    /**
     * 请求响应头集合
     */
    protected volatile ForestHeaderMap headers = new ForestHeaderMap();

    /**
     * 请求发送过程中可能产生的异常
     */
    protected volatile Throwable exception;

    /**
     * 响应内容反序列化成对象后的结果
     */
    protected volatile T result;


    public ForestResponse(ForestRequest request, Date requestTime, Date responseTime) {
        this.request = request;
        this.requestTime = requestTime;
        this.responseTime = responseTime;
    }


    /**
     * 获取该响应对象对应的请求对象
     *
     * @return 请求对象, {@link ForestRequest}类实例
     */
    public ForestRequest getRequest() {
        return request;
    }

    /**
     * 获取请求开始时间
     *
     * @return 请求开始时间, {@link Date}对象实例
     */
    public Date getRequestTime() {
        return requestTime;
    }

    /**
     * 获取响应接受时间
     *
     * @return 响应接受时间, {@link Date}对象实例
     */
    public Date getResponseTime() {
        return responseTime;
    }

    /**
     * 获取网络请求的耗时（以毫秒为单位）
     *
     * @return 从请求开始到接受到响应的整个耗费的时间, 单位：毫秒
     */
    public long getTimeAsMillisecond() {
        return responseTime.getTime() - requestTime.getTime();
    }

    /**
     * 该响应是否已打过日志
     *
     * @return {@code true}: 已打印过， {@code false}: 没打印过
     */
    public boolean isLogged() {
        return logged;
    }

    /**
     * 设置该响应是否已打过日志
     * @param logged {@code true}: 已打印过， {@code false}: 没打印过
     */
    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    /**
     * 获取下载文件名
     * @return 文件名
     */
    public String getFilename() {
        ForestHeader header = getHeader("Content-Disposition");
        if (header != null) {
            String dispositionValue = header.getValue();
            if (StringUtils.isNotEmpty(dispositionValue)) {
                String[] disGroup = dispositionValue.split(";");
                for (int i = disGroup.length - 1; i >= 0 ; i--) {
                    String disStr = disGroup[i];
                    if (disStr.startsWith("filename=")) {
                        return disStr.substring("filename=".length());
                    }
                }
            }
        }
        return request.getFilename();
    }

    /**
     * 获取请求响应内容文本
     * 和{@link ForestResponse#readAsString()}不同的地方在于，
     * {@link ForestResponse#getContent()}不会读取二进制形式数据内容，
     * 而{@link ForestResponse#readAsString()}会将二进制数据转换成字符串读取
     *
     * @return 响应内容文本字符串
     */
    public String getContent() {
        if (content != null) {
            return content;
        }
        content = readAsString();
        return content;
    }

    /**
     * 以字符串方式读取请求响应内容
     *
     * @return 请求响应内容字符串
     */
    public String readAsString() {
        try {
            byte[] bytes = getByteArray();
            if (bytes == null) {
                return null;
            }
            return byteToString(bytes);
        } catch (Exception e) {
            throw new ForestRuntimeException(e);
        }
    }

    public synchronized void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取反序列化成对象类型的请求响应内容
     *
     * @return 反序列化成对象类型的请求响应内容
     */
    public T getResult() {
        return result;
    }

    /**
     * 设置反序列化成对象类型的请求响应内容
     *
     * @param result 反序列化成对象类型的请求响应内容
     */
    public void setResult(T result) {
        this.result = result;
    }

    /**
     * 获取请求发送过程中的异常
     *
     * @return 异常对象, {@link Throwable}类及其子类实例
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * 设置请求发送过程中的异常
     *
     * @param exception 异常对象, {@link Throwable}类及其子类实例
     */
    public void setException(Throwable exception) {
        this.exception = exception;
    }

    /**
     * 获取请求响应的状态码
     *
     * @return 请求响应的状态码
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * 设置请求响应的状态码
     *
     * @param statusCode 请求响应的状态码
     */
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * 获取请求响应内容的数据类型
     *
     * @return 请求响应内容的数据类型, {@link ContentType}类实例
     */
    public ContentType getContentType() {
        return contentType;
    }

    /**
     * 设置请求响应内容的数据类型
     *
     * @param contentType 请求响应内容的数据类型, {@link ContentType}类实例
     */
    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    /**
     * 获取请求响应内容的数据编码
     *
     * @return 请求响应内容的数据编码名称
     */
    public String getContentEncoding() {
        return contentEncoding;
    }

    /**
     * 获取请求响应内容的数据长度
     *
     * @return 请求响应内容的数据长度
     */
    public long getContentLength() {
        return contentLength;
    }

    /**
     * 网络请求是否成功
     *
     * @return {@code true}: 成功， {@code false}: 失败
     */
    public boolean isSuccess() {
        return getException() == null
                && getStatusCode() >= HttpStatus.OK
                && getStatusCode() < HttpStatus.MULTIPLE_CHOICES;
    }

    /**
     * 网络请求是否失败
     *
     * @return {@code true}: 失败
     */
    public boolean isError() {
        return !isSuccess();
    }

    /**
     * 是否已接受到响应数据
     *
     * @return {@code true}: 已接收到， {@code false}: 未接收到
     */
    public abstract boolean isReceivedResponseData();

    /**
     * 以字节数组的形式获取请求响应内容
     *
     * @return 字节数组形式的响应内容
     * @throws Exception 读取字节数组过程中可能的异常
     */
    public abstract byte[] getByteArray() throws Exception;

    /**
     * 以输入流的形式获取请求响应内容
     *
     * @return 输入流形式的响应内容, {@link InputStream}实例
     * @throws Exception
     */
    public InputStream getInputStream() throws Exception {
        return new ByteArrayInputStream(getByteArray());
    }

    /**
     * 根据响应头名称获取单个请求响应头
     *
     * @param name 响应头名称
     * @return 请求响应头, {@link ForestHeader}类实例
     */
    public ForestHeader getHeader(String name) {
        return headers.getHeader(name);
    }

    /**
     * 根据响应头名称获取请求响应头列表
     *
     * @param name 响应头名称
     * @return 请求响应头列表
     * @see ForestHeader
     */
    public List<ForestHeader> getHeaders(String name) {
        return headers.getHeaders(name);
    }

    /**
     * 根据响应头名称获取请求响应头值
     *
     * @param name 响应头名称
     * @return 请求响应头值
     */
    public String getHeaderValue(String name) {
        return headers.getValue(name);
    }

    /**
     * 根据响应头名称获取请求响应头值列表
     *
     * @param name 响应头名称
     * @return 请求响应头值列表
     */
    public List<String> getHeaderValues(String name) {
        return headers.getValues(name);
    }

    /**
     * 获取请求响应的所有响应头
     *
     * @return 请求响应头表, {@link ForestHeaderMap}类实例
     */
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
