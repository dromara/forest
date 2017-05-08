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

package org.forest.config;


import org.forest.converter.ForestConverter;
import org.forest.converter.JSONConvertSelector;
import org.forest.converter.json.ForestFastjsonConverter;
import org.forest.converter.json.ForestGsonConverter;
import org.forest.converter.json.ForestJacksonConverter;
import org.forest.converter.json.ForestJsonConverter;
import org.forest.executors.ForestExecutorFactory;
import org.forest.executors.httpclient.HttpclientExecutorFactory;
import org.forest.utils.ForestDataType;
import org.forest.utils.RequestNameValue;
import org.forest.proxy.ProxyFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * global configuration
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-03-24
 */
public class ForestConfiguration implements Serializable {

    private String id;

    /**
     * maximum number of conntections allowed
     */
    private Integer maxConnections;

    /**
     * maximum number of connections allowed per route
     */
    private Integer maxRouteConnections;

    /**
     * timeout in milliseconds
     */
    private Integer timeout;

    /**
     * connect timeout in milliseconds
     */
    private Integer connectTimeout;

    /**
     * count of retry times
     */
    private Integer retryCount;

    private ForestExecutorFactory executorFactory;

    private List<RequestNameValue> defaultParameters;

    private List<RequestNameValue> defaultHeaders;

    private Map<ForestDataType, ForestConverter> converterMap;

    private JSONConvertSelector jsonConvertSelector;


    private Map<String, Object> variables = new HashMap<String, Object>();

    public ForestConfiguration() {
    }

    public static ForestConfiguration configuration() {
        ForestConfiguration configuration = new ForestConfiguration();
        configuration.setJsonConvertSelector(new JSONConvertSelector());
        setupJSONConverter(configuration);
        setupExecutorFactory(configuration);
        configuration.setTimeout(3000);
        configuration.setConnectTimeout(2000);
        configuration.setMaxConnections(500);
        configuration.setMaxRouteConnections(500);
        return configuration;
    }

    private static void setupExecutorFactory(ForestConfiguration configuration) {
        configuration.executorFactory = new HttpclientExecutorFactory(configuration);
    }

    private static void setupJSONConverter(ForestConfiguration configuration) {
        JSONConvertSelector jsonConvertSelector = new JSONConvertSelector();
        configuration.setJsonConverter(jsonConvertSelector.select());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
    }

    public Integer getMaxRouteConnections() {
        return maxRouteConnections;
    }

    public void setMaxRouteConnections(Integer maxRouteConnections) {
        this.maxRouteConnections = maxRouteConnections;
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

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public List<RequestNameValue> getDefaultParameters() {
        return defaultParameters;
    }

    public void setDefaultParameters(List<RequestNameValue> defaultParameters) {
        this.defaultParameters = defaultParameters;
    }

    public List<RequestNameValue> getDefaultHeaders() {
        return defaultHeaders;
    }

    public void setDefaultHeaders(List<RequestNameValue> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
    }

    public void setJsonConverter(ForestJsonConverter converter) {
        getConverterMap().put(ForestDataType.JSON, converter);
    }

    public ForestJsonConverter getJsonCoverter() {
        return (ForestJsonConverter) getConverterMap().get(ForestDataType.JSON);
    }

    public <T> ProxyFactory<T> getProxyFactory(Class<T> clazz) {
        return new ProxyFactory<T>(this, clazz);
    }

    public ForestExecutorFactory getExecutorFactory() {
        return executorFactory;
    }

    public void setVariableValue(String name, Object value) {
        getVariables().put(name, value);
    }

    public Object getVariableValue(String name) {
        return getVariables().get(name);
    }

    public ForestConverter getConverter(ForestDataType dataType) {
        return getConverterMap().get(dataType);
    }

    public Map<ForestDataType, ForestConverter> getConverterMap() {
        if (converterMap == null) {
            converterMap = new HashMap<ForestDataType, ForestConverter>();
        }
        return converterMap;
    }

    public void setConverterMap(Map<ForestDataType, ForestConverter> converterMap) {
        this.converterMap = converterMap;
    }


    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }


    public <T> T createInstance(Class<T> clazz) {
        ProxyFactory<T> proxyFactory = getProxyFactory(clazz);
        return proxyFactory.createInstance();
    }

    public JSONConvertSelector getJsonConvertSelector() {
        return jsonConvertSelector;
    }

    public void setJsonConvertSelector(JSONConvertSelector jsonConvertSelector) {
        this.jsonConvertSelector = jsonConvertSelector;
    }
}
