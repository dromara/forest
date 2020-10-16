package com.thebeastshop.forest.springboot.properties;

import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.ForestLogHandler;
import com.dtflys.forest.retryer.BackOffRetryer;
import com.dtflys.forest.ssl.SSLUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "forest", ignoreUnknownFields = true)
public class ForestConfigurationProperties {

    /**
     * Enable forest, default true
     */
//    private boolean enabled = true;

    /**
     * spring bean id of forest configuration
     */
    private String beanId;

    /**
     * maximum number of conntections allowed
     */
    private int maxConnections = 500;

    /**
     * maximum number of connections allowed per route
     */
    private int maxRouteConnections = 500;

    /**
     * timeout in milliseconds
     */
    private int timeout = 3000;

    /**
     * connect timeout in milliseconds
     */
    private int connectTimeout = 2000;

    /**
     * request charset
     */
    private String charset = "UTF-8";

    /**
     * Class of retryer
     */
    private Class retryer = BackOffRetryer.class;

    /**
     * count of retry times
     */
    private Integer retryCount = 0;

    /**
     * max interval of retrying request
     */
    private long maxRetryInterval = 0;

    /**
     * Enable print log of request/response
     */
    private boolean logEnabled = true;

    /**
     * Enable print log of request
     */
    private boolean logRequest = true;

    /**
     * Enable print log of response status
     */
    private boolean logResponseStatus = true;

    /**
     * Enable print log of response content
     */
    private boolean logResponseContent = false;

    /**
     * Class of log handler
     */
    private Class<? extends ForestLogHandler> logHandler = DefaultLogHandler.class;

    /**
     * default SSL protocol for https requests
     */
    private String sslProtocol = SSLUtils.TLSv1_2;

    /**
     * backend of forest: httpclient, okhttp3
     */
    private String backend = "okhttp3";

    /**
     * global variables
     */
    private Map<String, Object> variables = new HashMap<>();

    /**
     * Class list of interceptors
     */
    private List<Class> interceptors = new ArrayList<>();

    /**
     * SSL Key Stores
     */
    private List<ForestSSLKeyStoreProperties> sslKeyStores = new ArrayList<>();

/*
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
*/

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMaxRouteConnections() {
        return maxRouteConnections;
    }

    public void setMaxRouteConnections(int maxRouteConnections) {
        this.maxRouteConnections = maxRouteConnections;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
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

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

    public boolean isLogRequest() {
        return logRequest;
    }

    public void setLogRequest(boolean logRequest) {
        this.logRequest = logRequest;
    }

    public boolean isLogResponseStatus() {
        return logResponseStatus;
    }

    public void setLogResponseStatus(boolean logResponseStatus) {
        this.logResponseStatus = logResponseStatus;
    }

    public boolean isLogResponseContent() {
        return logResponseContent;
    }

    public void setLogResponseContent(boolean logResponseContent) {
        this.logResponseContent = logResponseContent;
    }

    public Class<? extends ForestLogHandler> getLogHandler() {
        return logHandler;
    }

    public void setLogHandler(Class<? extends ForestLogHandler> logHandler) {
        this.logHandler = logHandler;
    }

    public String getSslProtocol() {
        return sslProtocol;
    }

    public void setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }

    public String getBackend() {
        return backend;
    }

    public void setBackend(String backend) {
        this.backend = backend;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public List<Class> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<Class> interceptors) {
        this.interceptors = interceptors;
    }

    public List<ForestSSLKeyStoreProperties> getSslKeyStores() {
        return sslKeyStores;
    }

    public void setSslKeyStores(List<ForestSSLKeyStoreProperties> sslKeyStores) {
        this.sslKeyStores = sslKeyStores;
    }
}
