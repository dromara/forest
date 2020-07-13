package com.thebeastshop.forest.springboot.properties;

import com.dtflys.forest.ssl.SSLUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "forest", ignoreUnknownFields = true)
public class ForestConfigurationProperties {

    /**
     * Enable forest
     */
    private boolean enabled = false;

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
     * count of retry times
     */
    private int retryCount = 0;

    /**
     * default SSL protocol for https requests
     */
    private String sslProtocol = SSLUtils.TLSv1_2;

    /**
     * backend of forest: httpclient, okhttp3
     */
    private String backend = "httpclient";

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

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

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
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
