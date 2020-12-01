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

package com.dtflys.forest.config;


import com.dtflys.forest.converter.auto.DefaultAutoConverter;
import com.dtflys.forest.converter.binary.DefaultBinaryConverter;
import com.dtflys.forest.converter.text.DefaultTextConverter;
import com.dtflys.forest.interceptor.DefaultInterceptorFactory;
import com.dtflys.forest.interceptor.InterceptorFactory;
import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.ForestLogHandler;
import com.dtflys.forest.proxy.ProxyFactory;
import com.dtflys.forest.retryer.BackOffRetryer;
import com.dtflys.forest.ssl.SSLKeyStore;
import com.dtflys.forest.ssl.SSLUtils;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.backend.HttpBackendSelector;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.JSONConverterSelector;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.converter.xml.ForestJaxbConverter;
import com.dtflys.forest.converter.xml.ForestXmlConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.filter.Filter;
import com.dtflys.forest.filter.JSONFilter;
import com.dtflys.forest.filter.XmlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * global configuration
 * Forest全局配置管理类
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-03-24
 */
public class ForestConfiguration implements Serializable {

    private static Logger log = LoggerFactory.getLogger(ForestConfiguration.class);

    /**
     * 默认全局配置，再用户没有定义任何全局配置时使用该默认配置
     */
    private static ForestConfiguration defaultConfiguration = configuration();

    /**
     * 请求接口的实例缓存，用于缓存请求接口的动态代理的实例
     */
    private Map<Class, Object> instanceCache = new HashMap<>();

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
     * 全局的请求超时时间，单位为毫秒
     */
    private Integer timeout;

    /**
     * 全局的请求数据字符集
     */
    private String charset = "UTF-8";

    /**
     * 局的请求连接超时时间，单位为毫秒
     */
    private Integer connectTimeout;

    /**
     * 全局的请求失败重试策略类
     */
    private Class retryer;

    /**
     * 全局的最大请求失败重试次数
     */
    private Integer retryCount;

    /**
     * 全局的最大请求重试之间的时间间隔，单位为毫秒
     */
    private long maxRetryInterval;

    /**
     * 全局的单向HTTPS请求的SSL协议，默认为 TLSv1.2
     */
    private String sslProtocol = SSLUtils.TLS_1_2;

    /**
     * 是否允许打印请求/响应日志
     */
    private boolean logEnabled = true;

    /**
     * 是否允许打印请求/响应日志
     */
    private boolean logRequest = true;


    /**
     * 是否允许打印响应状态日志
     */
    private boolean logResponseStatus = true;

    /**
     * 是否允许打印响应内容日志
     */
    private boolean logResponseContent = false;

    /**
     * 日志处理器
     */
    private ForestLogHandler logHandler;

    /**
     * 是否缓存请求接口实例
     */
    private boolean cacheEnabled = true;

    /**
     * HTTP后端
     */
    private volatile HttpBackend backend;

    /**
     * HTTP后端名称
     * <p>现有后端包括：okhttp3 和 httpclient 两个</p>
     */
    private String backendName;

    /**
     * 全局默认请求参数列表
     */
    private List<RequestNameValue> defaultParameters;

    /**
     * 全局默认请求头信息
     */
    private List<RequestNameValue> defaultHeaders;

    /**
     * 全局拦截器列表
     */
    private List<Class> interceptors;

    /**
     * 全局数据转换器表
     */
    private Map<ForestDataType, ForestConverter> converterMap;

    /**
     * 全局JSON数据转换器选择器
     */
    private JSONConverterSelector jsonConverterSelector;

    /**
     * 拦截器工厂
     */
    private InterceptorFactory interceptorFactory;

    /**
     * HTTP后端选择器
     */
    private HttpBackendSelector httpBackendSelector = new HttpBackendSelector();

    /**
     * 全局过滤器注册表
     */
    private Map<String, Class> filterRegisterMap = new HashMap<>();

    /**
     * 全局变量表
     */
    private Map<String, Object> variables = new HashMap<String, Object>();

    /**
     * SSL的Key Store集合
     */
    private Map<String, SSLKeyStore> sslKeyStores = new HashMap<>();

    private ForestConfiguration() {
    }


    public static ForestConfiguration getDefaultConfiguration() {
        return defaultConfiguration;
    }

    /**
     * 实例化ForestConfiguration对象，并初始化默认值
     * @return 新创建的ForestConfiguration实例
     */
    public static ForestConfiguration configuration() {
        ForestConfiguration configuration = new ForestConfiguration();
        configuration.setId("forestConfiguration" + configuration.hashCode());
        configuration.setJsonConverterSelector(new JSONConverterSelector());
        configuration.setXmlConverter(new ForestJaxbConverter());
        configuration.setTextConverter();
        configuration.getConverterMap().put(ForestDataType.AUTO, new DefaultAutoConverter(configuration));
        configuration.getConverterMap().put(ForestDataType.BINARY, new DefaultBinaryConverter());
        setupJSONConverter(configuration);
        configuration.setTimeout(3000);
        configuration.setConnectTimeout(2000);
        configuration.setMaxConnections(500);
        configuration.setMaxRouteConnections(500);
        configuration.setRetryer(BackOffRetryer.class);
        configuration.setRetryCount(0);
        configuration.setMaxRetryInterval(0);
        configuration.setSslProtocol(SSLUtils.TLS_1_2);
        configuration.registerFilter("json", JSONFilter.class);
        configuration.registerFilter("xml", XmlFilter.class);
        configuration.setLogHandler(new DefaultLogHandler());
        return configuration;
    }

    /**
     * 获取请求接口实例缓存，返回的缓存对象集合用于缓存请求接口的动态代理的实例
     * @return 缓存对象集合
     */
    public Map<Class, Object> getInstanceCache() {
        return instanceCache;
    }

    /**
     * 配置HTTP后端
     * @return 当前ForestConfiguration实例
     */
    private ForestConfiguration setupBackend() {
        setBackend(httpBackendSelector.select(this));
//        log.info("[Forest] Http Backend: " + this.backend.getName());
        return this;
    }

    /**
     * 设置HTTP后端
     * @param backend HTTP后端对象
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setBackend(HttpBackend backend) {
        if (backend != null) {
            backend.init(this);
            log.info("[Forest] Http Backend: " + backend.getName());
        }
        this.backend = backend;
        return this;
    }

    /**
     * 设置HTTP后端名称
     * @param backendName HTTP后端名称
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setBackendName(String backendName) {
        this.backendName = backendName;
        return this;
    }

    /**
     * 获取HTTP后端名称
     * @return HTTP后端名称
     */
    public String getBackendName() {
        return backendName;
    }

    /**
     * 获取当前HTTP后端
     * @return 当前HTTP后端
     */
    public HttpBackend getBackend() {
        if (backend == null) {
            synchronized (this) {
                if (backend == null) {
                    setupBackend();
                }
            }
        }
        return backend;
    }

    /**
     * 获取拦截器工厂
     * @return 拦截器工厂
     */
    public InterceptorFactory getInterceptorFactory() {
        if (interceptorFactory == null) {
            synchronized (this) {
                if (interceptorFactory == null) {
                    interceptorFactory = new DefaultInterceptorFactory();
                }
            }
        }
        return interceptorFactory;
    }

    /**
     * 设置拦截器工厂
     * @param interceptorFactory 拦截器工厂
     */
    public void setInterceptorFactory(InterceptorFactory interceptorFactory) {
        this.interceptorFactory = interceptorFactory;
    }

    /**
     * 设置HTTP后端选择器
     * @param httpBackendSelector HTTP后端选择器
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setHttpBackendSelector(HttpBackendSelector httpBackendSelector) {
        this.httpBackendSelector = httpBackendSelector;
        return this;
    }

    /**
     * 配置JSON数据转换器
     * @param configuration 全局配置对象
     */
    private static void setupJSONConverter(ForestConfiguration configuration) {
        configuration.setJsonConverter(configuration.jsonConverterSelector.select());
    }

    /**
     * 获取全局配置ID
     * @return 全局配置ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置全局配置ID
     * @param id 全局配置ID
     * @return 当前ForestConfiguration实例
     */
    private ForestConfiguration setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * 获取全局的最大连接数
     * @return 最大连接数
     */
    public Integer getMaxConnections() {
        return maxConnections;
    }

    /**
     * 设置全局的最大连接数
     * @param maxConnections 全局的最大连接数
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
        return this;
    }

    /**
     * 获取全局的最大请求路径连接数
     * @return 最大请求路径连接数
     */
    public Integer getMaxRouteConnections() {
        return maxRouteConnections;
    }

    /**
     * 设置全局的最大请求路径连接数
     * @param maxRouteConnections 最大请求路径连接数
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setMaxRouteConnections(Integer maxRouteConnections) {
        this.maxRouteConnections = maxRouteConnections;
        return this;
    }

    /**
     * 获取全局的请求超时时间，单位为毫秒
     * @return 请求超时时间，单位为毫秒
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * 设置全局的请求超时时间，单位为毫秒
     * @param timeout 请求超时时间，单位为毫秒
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setTimeout(Integer timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 获取全局的请求数据字符集
     * @return 字符集名称
     */
    public String getCharset() {
        return charset;
    }

    /**
     * 设置全局的请求数据字符集
     * @param charset 字符集名称
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * 获取全局的请求连接超时时间，单位为毫秒
     * @return 连接超时时间，单位为毫秒
     */
    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 设置全局的请求连接超时时间，单位为毫秒
     * @param connectTimeout 连接超时时间，单位为毫秒
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * 获取全局的请求失败重试策略类
     * @return 重试策略类
     */
    public Class getRetryer() {
        return retryer;
    }

    /**
     * 设置全局的请求失败重试策略类
     * @param retryer 重试策略类
     */
    public void setRetryer(Class retryer) {
        this.retryer = retryer;
    }

    /**
     * 获取全局的最大请求失败重试次数
     * @return 重试次数
     */
    public Integer getRetryCount() {
        return retryCount;
    }

    /**
     * 设置全局的最大请求失败重试次数
     * @param retryCount 重试次数
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    /**
     * 获取全局的最大请求重试之间的时间间隔，单位为毫秒
     * @return 最大请求重试之间的时间间隔
     */
    public long getMaxRetryInterval() {
        return maxRetryInterval;
    }

    /**
     * 设置全局的最大请求重试之间的时间间隔，单位为毫秒
     * @param maxRetryInterval 最大请求重试之间的时间间隔
     */
    public void setMaxRetryInterval(long maxRetryInterval) {
        this.maxRetryInterval = maxRetryInterval;
    }

    /**
     * 获取全局的单向HTTPS请求的SSL协议，默认为 TLSv1.2
     * @return SSL协议名称
     */
    public String getSslProtocol() {
        return sslProtocol;
    }

    /**
     * 设置全局的单向HTTPS请求的SSL协议
     * @param sslProtocol SSL协议名称
     */
    public void setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }

    /**
     * 是否允许打印请求日志
     * @return 允许为 {@code true} , 否则为 {@code false}
     */
    public boolean isLogEnabled() {
        return logEnabled;
    }

    /**
     * 设置是否允许打印请求日志
     * @param logEnabled 允许为 {@code true} , 否则为 {@code false}
     */
    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

    /**
     * 是否允许打印请求/响应日志
     * @return 允许为 {@code true}, 否则为 {@code false}
     */
    public boolean isLogRequest() {
        return logRequest;
    }

    /**
     * 设置是否允许打印请求/响应日志
     * @param logRequest 允许为 {@code true} , 否则为 {@code false}
     */
    public void setLogRequest(boolean logRequest) {
        this.logRequest = logRequest;
    }

    /**
     * 是否允许打印响应日志
     * @return 允许为 {@code true}, 否则为 {@code false}
     */
    public boolean isLogResponseStatus() {
        return logResponseStatus;
    }

    /**
     * 设置是否允许打印响应日志
     * @param logResponseStatus 允许为 {@code true}, 否则为 {@code false}
     */
    public void setLogResponseStatus(boolean logResponseStatus) {
        this.logResponseStatus = logResponseStatus;
    }

    /**
     * 是否允许打印响应日志
     * @return 允许为 {@code true}, 否则为 {@code false}
     */
    public boolean isLogResponseContent() {
        return logResponseContent;
    }

    /**
     * 设置是否允许打印响应日志
     * @param logResponseContent 允许为 {@code true}, 否则为 {@code false}
     */
    public void setLogResponseContent(boolean logResponseContent) {
        this.logResponseContent = logResponseContent;
    }

    /**
     * 获取日志处理器
     * @return 日志处理器接口实例
     */
    public ForestLogHandler getLogHandler() {
        return logHandler;
    }

    /**
     * 设置日志处理器
     * @param logHandler 日志处理器接口实例
     */
    public void setLogHandler(ForestLogHandler logHandler) {
        this.logHandler = logHandler;
    }

    /**
     * 是否缓存请求接口实例
     * @return 如果允许缓存实例为 {@code true}, 否则为 {@code false}
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    /**
     * 设置是否缓存请求接口实例
     * @param cacheEnabled 如果允许缓存实例为 {@code true}, 否则为 {@code false}
     */
    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    /**
     * 获取全局默认请求参数列表
     * @return {@link RequestNameValue} 对象列表
     */
    public List<RequestNameValue> getDefaultParameters() {
        return defaultParameters;
    }

    /**
     * 设置全局默认请求参数列表
     * @param defaultParameters 请求参数列表
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setDefaultParameters(List<RequestNameValue> defaultParameters) {
        this.defaultParameters = defaultParameters;
        return this;
    }

    /**
     * 获取全局默认请求头信息列表
     * @return {@link RequestNameValue} 对象列表
     */
    public List<RequestNameValue> getDefaultHeaders() {
        return defaultHeaders;
    }

    /**
     * 设置全局默认请求头信息列表
     * @param defaultHeaders 请求头信息列表
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setDefaultHeaders(List<RequestNameValue> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    /**
     * 获取全局拦截器列表
     * @return {@link Class} 实例列表
     */
    public List<Class> getInterceptors() {
        return interceptors;
    }

    /**
     * 设置全局拦截器列表
     * @param interceptors 全局拦截器列表
     */
    public void setInterceptors(List<Class> interceptors) {
        this.interceptors = interceptors;
    }

    /**
     * 设置全局JSON数据转换器
     * @param converter JSON数据转换器
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setJsonConverter(ForestJsonConverter converter) {
        getConverterMap().put(ForestDataType.JSON, converter);
        return this;
    }

    /**
     * 获取当前全局JSON数据转换器
     * @return JSON数据转换器
     */
    public ForestJsonConverter getJsonConverter() {
        return (ForestJsonConverter) getConverterMap().get(ForestDataType.JSON);
    }

    /**
     * 设置全局XML数据转换器
     * @param converter XML数据转换器
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setXmlConverter(ForestXmlConverter converter) {
        getConverterMap().put(ForestDataType.XML, converter);
        return this;
    }

    /**
     * 获取当前全局XML数据转换器
     * @return XML数据转换器
     */
    public ForestXmlConverter getXmlConverter() {
        return (ForestXmlConverter) getConverterMap().get(ForestDataType.XML);
    }

    /**
     * 设置默认的全局文本数据转换器
     * @return 当前ForestConfiguration实例
     */
    private ForestConfiguration setTextConverter() {
        getConverterMap().put(ForestDataType.TEXT, new DefaultTextConverter());
        return this;
    }

    /**
     * 根据请求接口类创建并获取请求接口的动态代理工厂
     * @param clazz 请求接口类
     * @param <T>   请求接口类泛型
     * @return      动态代理工厂
     */
    public <T> ProxyFactory<T> getProxyFactory(Class<T> clazz) {
        return new ProxyFactory<T>(this, clazz);
    }

    /**
     * 设置全局变量
     * @param name   变量名
     * @param value  变量值
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setVariableValue(String name, Object value) {
        getVariables().put(name, value);
        return this;
    }

    /**
     * 根据变量名获取全局变量值
     * @param name   变量名
     * @return       变量值
     */
    public Object getVariableValue(String name) {
        return getVariables().get(name);
    }

    /**
     * 获取全局SSL的Key Store表
     * @return SSL的Key Store表
     */
    public Map<String, SSLKeyStore> getSslKeyStores() {
        return sslKeyStores;
    }

    /**
     * 设置全局SSL的Key Store表
     * @param sslKeyStores SSL的Key Store表
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setSslKeyStores(Map<String, SSLKeyStore> sslKeyStores) {
        this.sslKeyStores = sslKeyStores;
        return this;
    }

    /**
     * 注册全局SSL的Key Store信息
     * @param keyStore Key Store信息
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration registerKeyStore(SSLKeyStore keyStore) {
        sslKeyStores.put(keyStore.getId(), keyStore);
        return this;
    }

    /**
     * 根据ID获取全局SSL的Key Store信息
     * @param id Key Store的ID
     * @return {@link SSLKeyStore} 实例
     */
    public SSLKeyStore getKeyStore(String id) {
        return sslKeyStores.get(id);
    }

    /**
     * 根据请求响应数据类型获取数据转换器
     * @param dataType 请求响应数据类型
     * @return 数据转换器
     */
    public ForestConverter getConverter(ForestDataType dataType) {
        ForestConverter converter = getConverterMap().get(dataType);
        if (converter == null) {
            throw new ForestRuntimeException("Can not found converter for type " + dataType.getName());
        }
        return converter;
    }

    /**
     * 获取全局数据转换器表
     * @return 数据转换器表
     */
    public Map<ForestDataType, ForestConverter> getConverterMap() {
        if (converterMap == null) {
            converterMap = new HashMap<ForestDataType, ForestConverter>();
        }
        return converterMap;
    }

    /**
     * 设置全局数据转换器表
     * @param converterMap 数据转换器表
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setConverterMap(Map<ForestDataType, ForestConverter> converterMap) {
        this.converterMap = converterMap;
        return this;
    }

     /**
     * 获取全局变量表
     * @return 全局变量表
     */
    public Map<String, Object> getVariables() {
        return variables;
    }

    /**
     * 设置全局变量表
     * @param variables 变量表
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setVariables(Map<String, Object> variables) {
        this.variables = variables;
        return this;
    }

    /**
     * 创建请求接口的动态代理实例
     * @param clazz  请求接口类
     * @param <T>    请求接口类泛型
     * @return       动态代理实例
     */
    public <T> T createInstance(Class<T> clazz) {
        ProxyFactory<T> proxyFactory = getProxyFactory(clazz);
        return proxyFactory.createInstance();
    }

    /**
     * 设置全局JSON数据转换器的选择器
     * @param jsonConverterSelector JSON转换器选择策略对象，{@link JSONConverterSelector}类实例
     * @return 当前ForestConfiguration实例
     */
    private ForestConfiguration setJsonConverterSelector(JSONConverterSelector jsonConverterSelector) {
        this.jsonConverterSelector = jsonConverterSelector;
        return this;
    }


    /**
     * 注册全局过滤器
     * @param name 过滤器名称
     * @param filterClass 过滤器类
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration registerFilter(String name, Class filterClass) {
        if (!(Filter.class.isAssignableFrom(filterClass))) {
            throw new ForestRuntimeException("Cannot register class \"" + filterClass.getName()
                    + "\" as a filter, filter class must implement Filter interface!");
        }
        if (filterRegisterMap.containsKey(name)) {
            throw new ForestRuntimeException("filter \"" + name + "\" already exists!");
        }
        filterRegisterMap.put(name, filterClass);
        return this;
    }

    /**
     * 获取全局过滤器注册表中所有过滤器的名称列表
     * @return 过滤器的名称列表
     */
    public List<String> getRegisteredFilterNames() {
        Set<String> nameSet = filterRegisterMap.keySet();
        List<String> names = new ArrayList<>();
        Iterator<String> iterator = nameSet.iterator();
        for (; iterator.hasNext(); ) {
            names.add(iterator.next());
        }
        return names;
    }

    /**
     * 判断全局过滤器注册表是否已存在某个过滤器
     * @param name 过滤器名称
     * @return 如果存在为 {@code true}, 否则为 {@code false}
     */
    public boolean hasFilter(String name) {
        return filterRegisterMap.containsKey(name);
    }

    /**
     * 根据过滤器名称创建新的过滤器实例
     * @param name 过滤器名称
     * @return 新的Filter实例
     */
    public Filter newFilterInstance(String name) {
        Class filterClass = filterRegisterMap.get(name);
        if (filterClass == null) {
            throw new ForestRuntimeException("filter \"" + name + "\" does not exists!");
        }
        try {
            return (Filter) filterClass.newInstance();
        } catch (InstantiationException e) {
            throw new ForestRuntimeException("An error occurred the initialization of filter \"" + name + "\" ! cause: " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new ForestRuntimeException("An error occurred the initialization of filter \"" + name + "\" ! cause: " + e.getMessage(), e);
        }
    }

}
