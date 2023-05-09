package org.dromara.forest.reflection;

import org.dromara.forest.converter.ForestConverter;
import org.dromara.forest.converter.ForestEncoder;
import org.dromara.forest.logging.LogConfiguration;
import org.dromara.forest.utils.ForestDataType;

import java.lang.annotation.Annotation;

public class MetaRequest {

    private Annotation requestAnnotation;

    /**
     * target http url
     */
    private String url;

    /**
     * request body type: <br>
     *     FORM JSON XML TEXT PROTOBUF FILE BINARY
     */
    private String bodyType;

    /**
     * http method type: <br>
     *     GET POST PUT HEAD OPTIONS DELETE PATCH TRACE
     */
    private String type;

    /**
     * type of response data: <br>
     *     text json xml <br>
     * default value is "auto"
     */
    private String dataType;

    /**
     * whether can use async http request or not
     */
    private boolean async;

    /**
     * timeout
     */
    private Integer timeout;

    /**
     * connect timeout
     */
    private Integer connectTimeout;

    /**
     * read timeout
     */
    private Integer readTimeout;

    /**
     * SSL protocol
     */
    private String sslProtocol;

    /**
     * Class of retryer
     */
    private Class retryer;

    /**
     * max count to retry
     */
    private Integer retryCount;

    private Long maxRetryInterval;

    /**
     * Content Type
     */
    private String contentType;

    /**
     * Content Encoding
     */
    private String contentEncoding;

    /**
     * Charset, Default is UTF-8
     */
    private String charset;

    /**
     * Response Encoding
     * <p>响应内容的字符编码[UTF-8, GBK...]
     *  <p>优先根据该字段来确认字符编码格式,再根据如下顺序来获取
     *  <ul>
     *      <li>1. 从ContentType中获取</li>
     *      <li>2. 从响应头中的 Content-Encoding 获取</li>
     *      <li>3. 根据响应内容智能识别</li>
     *  </ul>
     */
    private String responseEncoding;

    /**
     * User Agent
     */
    private String userAgent;

    /**
     * Request Headers
     */
    private String[] headers;

    /**
     * 拦截器类数组
     */
    private Class<?>[] interceptor;

    private String[] data;

    private long progressStep;

    private Class<? extends ForestEncoder> encoder;

    private Class<? extends ForestConverter> decoder;

    /**
     * KeyStore Id
     */
    private String keyStore;

    /**
     * 是否允许打印请求/响应日志
     */
    private boolean logEnabled;

    /**
     * 日志配置信息 (比logEnabled属性优先级更高)
     */
    private LogConfiguration logConfiguration;

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

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public void setBodyType(ForestDataType bodyType) {
        this.bodyType = bodyType.getName();
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

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
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

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        if (retryCount >= 0) {
            this.retryCount = retryCount;
        } else {
            this.retryCount = null;
        }
    }

    public Long getMaxRetryInterval() {
        return maxRetryInterval;
    }

    public void setMaxRetryInterval(Long maxRetryInterval) {
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

    public String getResponseEncoding() {
        return responseEncoding;
    }

    public void setResponseEncoding(String responseEncoding) {
        this.responseEncoding = responseEncoding;
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

    public Class<? extends ForestEncoder> getEncoder() {
        return encoder;
    }

    public void setEncoder(Class<? extends ForestEncoder> encoder) {
        this.encoder = encoder;
    }

    public Class<? extends ForestConverter> getDecoder() {
        return decoder;
    }

    public void setDecoder(Class<? extends ForestConverter> decoder) {
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

    public LogConfiguration getLogConfiguration() {
        return logConfiguration;
    }

    public void setLogConfiguration(LogConfiguration logConfiguration) {
        this.logConfiguration = logConfiguration;
    }
}
