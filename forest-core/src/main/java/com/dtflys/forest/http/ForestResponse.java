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
import com.dtflys.forest.callback.SuccessWhen;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.ByteEncodeUtils;
import com.dtflys.forest.utils.GzipUtils;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.StringUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.List;

/**
 * Forest请求响应类
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.1.0
 */
public abstract class ForestResponse<T> extends ResultGetter implements HasURL {

    protected final static int MAX_BYTES_CAPACITY = 1024 * 1024 * 2;

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
     * 响应体是否已关闭
     */
    protected volatile boolean closed = false;

    /**
     * 是否为Gzip压缩
     */
    protected boolean isGzip = false;

    /**
     * 是否已经打印过响应日志
     */
    protected volatile boolean logged = false;

    /**
     * 响应是否成功
     */
    private volatile Boolean success = null;

    /**
     * 网络状态码
     */
    protected volatile Integer statusCode;

    /**
     * 原因短语
     */
    protected volatile String reasonPhrase;

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
     * 响应内容的编码字符集
     */
    protected volatile String charset;

    /**
     * 请求响应内容的数据长度
     */
    protected volatile long contentLength;

    /**
     * 请求响应头集合
     */
    protected volatile ForestHeaderMap headers = new ForestHeaderMap(this);

    /**
     * 请求发送过程中可能产生的异常
     */
    protected volatile Throwable exception;

    /**
     * 响应内容反序列化成对象后的结果
     */
    protected volatile T result;


    public ForestResponse(ForestRequest request, Date requestTime, Date responseTime) {
        super(request);
        this.request = request;
        this.requestTime = requestTime;
        this.responseTime = responseTime;
    }

    @Override
    protected ForestResponse getResponse() {
        return this;
    }

    /**
     * 获取响应请求的URL
     *
     * @return {@link ForestURL}对象实例
     * @since 1.5.23
     */
    @Override
    public ForestURL url() {
        if (request == null) {
            return null;
        }
        return request.url();
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
     * 请求是否以取消
     *
     * @return {@code true}: 请求已被取消; {@code false}: 未被取消
     * @since 1.5.27
     */
    public boolean isCanceled() {
        return request.isCanceled();
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
     *
     * @param logged {@code true}: 已打印过， {@code false}: 没打印过
     */
    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    /**
     * 是否是重定向响应
     *
     * @return {@code true}: 是重定向, {@code false}: 不是重定向
     */
    public boolean isRedirection() {
        return getStatusCode() > HttpStatus.MULTIPLE_CHOICES && getStatusCode() <= HttpStatus.TEMPORARY_REDIRECT;
    }

    /**
     * 获取重定向地址
     *
     * @return 重定向地址
     */
    public String getRedirectionLocation() {
        return getHeaderValue(ForestHeader.LOCATION);
    }

    /**
     * 获取重定向Forest请求
     *
     * @return Forest请求对象
     */
    public ForestRequest<T> redirectionRequest() {
        if (isRedirection() && request != null) {
            try {
                String location = getRedirectionLocation();
                if (StringUtils.isBlank(location)) {
                    return null;
                }
                ForestRequest<T> redirectRequest = request.clone();
                redirectRequest.clearQueries();
                redirectRequest.setUrl(location);
                redirectRequest.prevRequest = request;
                redirectRequest.prevResponse = this;
                return redirectRequest;
            } finally {
                close();
            }
        }
        return null;
    }

    /**
     * 获取下载文件名
     *
     * @return 文件名
     */
    public String getFilename() {
        ForestHeader header = getHeader("Content-Disposition");
        if (header != null) {
            String dispositionValue = header.getValue();
            if (StringUtils.isNotEmpty(dispositionValue)) {
                String[] disGroup = dispositionValue.split(";");
                for (int i = disGroup.length - 1; i >= 0; i--) {
                    /**
                     * content-disposition: attachment; filename="50db602db30cf6df60698510003d2415.jpg"
                     * need replace trim
                     */
                    String disStr = StringUtils.trimBegin(disGroup[i]);
                    if (disStr.startsWith("filename=")) {
                        String filename = disStr.substring("filename=".length());
                        if (filename.startsWith("\"") && filename.endsWith("\"")) {
                            filename = filename.substring(1, filename.length() - 1);
                        }
                        return filename;
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
                return "";
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
        if (result == null && isReceivedResponseData()) {
            Type type = request.getLifeCycleHandler().getResultType();
            if (type == null) {
                type = request.getMethod().getReturnType();
            }
            if (type == null) {
                try {
                    result = (T) get(String.class);
                } catch (Throwable th) {
                }
            } else {
                Class clazz = ReflectUtils.toClass(type);
                if (ForestResponse.class.isAssignableFrom(clazz)) {
                    Type argType = ReflectUtils.getGenericArgument(clazz);
                    if (argType == null) {
                        argType = String.class;
                    }
                    result = get(argType);
                } else {
                    result = get(type);
                }
            }
        }
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
     * 是否没有网络异常
     *
     * @return {@code true}: 没有异常, {@code false}: 有异常
     */
    public boolean noException() {
        return exception == null;
    }

    /**
     * 请求是否超时
     *
     * @return {@code true}: 已超时, {@code false}: 未超时
     */
    public boolean isTimeout() {
        if (noException()) {
            return false;
        }
        return exception instanceof SocketTimeoutException;
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
        if (statusCode == null) {
            return -1;
        }
        return statusCode;
    }

    /**
     * 获取请求响应的状态码
     * <p>同 {@link ForestResponse#getStatusCode()}
     *
     * @return 请求响应的状态码
     * @see ForestResponse#getStatusCode()
     */
    public int statusCode() {
        if (statusCode == null) {
            return -1;
        }
        return getStatusCode();
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
     * 获取请求响应的原因短语
     *
     * @return 请求响应的原因短语
     */
    public String getReasonPhrase() {
        return reasonPhrase;
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
     * <p>其判断过程如下：
     * <p>先判断 successWhen 回调函数
     * <p>再判断全局 successWhen 回调函数
     * <p>最后判断默认请求成功条件判断逻辑：
     * <ul>
     *     <li>1. 判断请求过程是否有异常</li>
     *     <li>2. 判断HTTP响应状态码是否在正常范围内(100 ~ 399)</li>
     * </ul>
     * <p>
     * 以上过程一个响应只会执行一次！执行过后被会缓存到 success 字段中
     * <p>下次再调用 isSuccess() 用是第一次执行的结果
     *
     * @return {@code true}: 请求成功， {@code false}: 请求失败
     */
    public boolean isSuccess() {
        if (success == null) {
            if (request != null) {
                if (request.getSuccessWhen() != null) {
                    success = request.getSuccessWhen().successWhen(request, this);
                } else {
                    SuccessWhen globalSuccessWhen = request.getConfiguration().getSuccessWhen();
                    if (globalSuccessWhen != null) {
                        success = globalSuccessWhen.successWhen(request, this);
                    }
                }
            }
            if (success == null) {
                success = noException() && statusOk();
            }
        }
        return success;
    }

    /**
     * 请求响应的状态码是否在 100 ~ 199 范围内
     *
     * @return {@code true}: 在 100 ~ 199 范围内, {@code false}: 不在
     */
    public boolean status_1xx() {
        return getStatusCode() >= HttpStatus.CONTINUE
                && getStatusCode() < HttpStatus.OK;
    }

    /**
     * 请求响应的状态码是否在 200 ~ 299 范围内
     *
     * @return {@code true}: 在 200 ~ 299 范围内, {@code false}: 不在
     */
    public boolean status_2xx() {
        return getStatusCode() >= HttpStatus.OK
                && getStatusCode() < HttpStatus.MULTIPLE_CHOICES;
    }

    /**
     * 请求响应的状态码是否在 300 ~ 399 范围内
     *
     * @return {@code true}: 在 300 ~ 399 范围内, {@code false}: 不在
     */
    public boolean status_3xx() {
        return getStatusCode() >= HttpStatus.MULTIPLE_CHOICES
                && getStatusCode() < HttpStatus.BAD_REQUEST;
    }

    /**
     * 请求响应的状态码是否在 400 ~ 499 范围内
     *
     * @return {@code true}: 在 400 ~ 499 范围内, {@code false}: 不在
     */
    public boolean status_4xx() {
        return getStatusCode() >= HttpStatus.BAD_REQUEST
                && getStatusCode() < HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * 请求响应的状态码是否在 500 ~ 599 范围内
     *
     * @return {@code true}: 在 500 ~ 599 范围内, {@code false}: 不在
     */
    public boolean status_5xx() {
        return getStatusCode() >= HttpStatus.INTERNAL_SERVER_ERROR
                && getStatusCode() < 600;
    }

    /**
     * 请求响应码是否在正常 100 ~ 399 范围内
     *
     * @return {@code true}: 在 100 ~ 399 范围内, {@code false}: 不在
     */
    public boolean statusOk() {
        return status_1xx() || status_2xx() || status_3xx();
    }

    /**
     * 请求响应码是否和输入参数相同
     *
     * @param statusCode 被比较的响应码
     * @return {@code true}: 相同, {@code false}: 不同
     */
    public boolean statusIs(int statusCode) {
        return getStatusCode() == statusCode;
    }

    /**
     * 请求响应码是否和输入参数不同
     *
     * @param statusCode 被比较的响应码
     * @return {@code true}: 不同, {@code false}: 相同
     */
    public boolean statusIsNot(int statusCode) {
        return getStatusCode() != statusCode;
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
     * @throws Exception 可能抛出的异常类型
     */
    public abstract InputStream getInputStream() throws Exception;

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
     * 从响应头中获取Cookie列表
     *
     * @return {@link ForestCookie}对象列表
     */
    public List<ForestCookie> getCookies() {
        return headers.getSetCookies();
    }

    /**
     * 根据Cookie名称获取Cookie
     *
     * @param name Cookie名称
     * @return {@link ForestCookie}对象实例
     * @since 1.5.23
     */
    public ForestCookie getCookie(String name) {
        return headers.getSetCookie(name);
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
     * 获取请求响应的内容编码字符集
     *
     * @return 编码字符集
     */
    public String getCharset() {
        return charset;
    }

    /**
     * 把字节数组转换成字符串（自动根据字符串编码转换）
     *
     * @param bytes 字节数组
     * @return 字符串
     * @throws IOException 字符串处理异常
     */
    protected String byteToString(byte[] bytes) throws IOException {
        if (StringUtils.isEmpty(charset)) {
            // Content-Encoding为空的情况下，自动判断字符编码
            charset = ByteEncodeUtils.getCharsetName(bytes);
        }
        if (isGzip) {
            try {
                return GzipUtils.decompressGzipToString(bytes, charset);
            } catch (Throwable th) {
                isGzip = false;
            }
        }
        char[] chs = charset.toCharArray();
        if (chs.length > 2 &&
                (chs[0] == 'g' || chs[0] == 'G') &&
                (chs[1] == 'b' || chs[1] == 'B')) {
            // 返回的GB中文编码会有多种编码类型，这里统一使用GBK编码
            charset = "GBK";
        }
        return IOUtils.toString(bytes, charset);
    }

    public boolean isClosed() {
        return closed;
    }

    public abstract void close();

}
