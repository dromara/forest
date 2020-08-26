package com.dtflys.forest.reflection;

import java.lang.annotation.Annotation;

public class MetaRequest {

    private Annotation requestAnnotation;

    /**
     * target http url
     * @return
     */
    private String url;

    /**
     * http method type: <br>
     *     GET POST PUT HEAD OPTIONS DELETE PATCH TRACE
     * @return
     */
    private String type;

    /**
     * type of response data: <br>
     *     text json xml <br>
     * default value is "auto"
     * @return
     */
    private String dataType;

    /**
     * whether can use async http request or not
     * @return
     */
    private boolean async;

    private int timeout;

    /**
     * SSL protocol
     */
    private String sslProtocol;

    /**
     * Class of retryer
     * @return
     */
    private Class retryer;

    /**
     * max count to retry
     * @return
     */
    private int retryCount;

    private long maxRetryInterval;

    /**
     * Content Type
     * @return
     */
    private String contentType;

    /**
     * Content Encoding
     * @return
     */
    private String contentEncoding;

    /**
     * Charset, Default is UTF-8
     * @return
     */
    private String charset;

    /**
     * User Agent
     */
    private String userAgent;

    private String[] headers;

    private Class<?>[] interceptor;

    private String[] data;

    private long progressStep;

    private Class<?> decoder;

    /**
     * KeyStore Id
     * @return
     */
    private String keyStore;

    private boolean logEnabled;

    public MetaRequest(Annotation requestAnnotation) {
        this.requestAnnotation = requestAnnotation;
    }

    public MetaRequest() {
    }

    public Annotation getRequestAnnotation() {
        return requestAnnotation;
    }

    public void setRequestAnnotation(Annotation requestAnnotation) {
        this.requestAnnotation = requestAnnotation;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getSslProtocol() {
        return sslProtocol;
    }

    public void setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }

    public Class getRetryer() {
        return retryer;
    }

    public void setRetryer(Class retryer) {
        this.retryer = retryer;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public long getMaxRetryInterval() {
        return maxRetryInterval;
    }

    public void setMaxRetryInterval(long maxRetryInterval) {
        this.maxRetryInterval = maxRetryInterval;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public Class<?>[] getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(Class<?>[] interceptor) {
        this.interceptor = interceptor;
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    public long getProgressStep() {
        return progressStep;
    }

    public void setProgressStep(long progressStep) {
        this.progressStep = progressStep;
    }

    public Class<?> getDecoder() {
        return decoder;
    }

    public void setDecoder(Class<?> decoder) {
        this.decoder = decoder;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }
}
