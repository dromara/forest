package com.dtflys.forest.http;

import com.dtflys.forest.backend.ContentType;

import java.io.Closeable;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface Res<T> extends ForestResultGetter, HasURL, HasHeaders {
    @Override
    ForestURL url();

    ForestRequest getRequest();

    Date getRequestTime();

    Date getResponseTime();

    long getTimeAsMillisecond();

    boolean isCanceled();

    boolean isLogged();

    void setLogged(boolean logged);

    boolean isRedirection();
    
    boolean isAutoClosable();

    String getRedirectionLocation();

    ForestRequest<T> redirectionRequest();

    String getFilename();

    String getContent();

    String readAsString();

    void setContent(String content);

    Optional<T> opt();

    Optional<T> getResultOpt();

    T getResult();

    void setResult(T result);

    Throwable getException();

    boolean noException();

    boolean isTimeout();

    void setException(Throwable exception);

    int getStatusCode();

    int statusCode();

    void setStatusCode(Integer statusCode);

    String getReasonPhrase();

    ContentType getContentType();

    void setContentType(ContentType contentType);

    String getContentEncoding();

    long getContentLength();

    boolean isSuccess();

    boolean status_1xx();

    boolean status_2xx();

    boolean status_3xx();

    boolean status_4xx();

    boolean status_5xx();

    boolean statusOk();

    boolean statusIs(int statusCode);

    boolean statusIsNot(int statusCode);

    boolean isError();

    /**
     * 是否已接受到响应数据
     *
     * @return {@code true}: 已接收到， {@code false}: 未接收到
     */
    boolean isReceivedResponseData();

    /**
     * 以字节数组的形式获取请求响应内容
     *
     * @return 字节数组形式的响应内容
     * @throws Exception 读取字节数组过程中可能的异常
     */
    byte[] getByteArray() throws Exception;

    /**
     * 以输入流的形式获取请求响应内容
     *
     * @return 输入流形式的响应内容, {@link InputStream}实例
     * @throws Exception 可能抛出的异常类型
     */
    InputStream getInputStream() throws Exception;

    ForestHeader getHeader(String name);

    List<ForestHeader> getHeaders(String name);

    List<ForestCookie> getCookies();

    ForestCookie getCookie(String name);

    String getHeaderValue(String name);

    List<String> getHeaderValues(String name);

    @Override
    ForestHeaderMap getHeaders();

    String getCharset();

    boolean isClosed();

    boolean isBytesRead();

    void close();
}
