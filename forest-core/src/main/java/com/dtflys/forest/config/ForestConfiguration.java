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

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Backend;
import com.dtflys.forest.annotation.BackendClient;
import com.dtflys.forest.annotation.BaseLifeCycle;
import com.dtflys.forest.annotation.BinaryBody;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.BodyType;
import com.dtflys.forest.annotation.FileBody;
import com.dtflys.forest.annotation.DecompressGzip;
import com.dtflys.forest.annotation.Delete;
import com.dtflys.forest.annotation.DeleteRequest;
import com.dtflys.forest.annotation.FormBody;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.GetRequest;
import com.dtflys.forest.annotation.HTTPProxy;
import com.dtflys.forest.annotation.HeadRequest;
import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.annotation.Headers;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.JSONQuery;
import com.dtflys.forest.annotation.LogEnabled;
import com.dtflys.forest.annotation.LogHandler;
import com.dtflys.forest.annotation.MethodLifeCycle;
import com.dtflys.forest.annotation.Options;
import com.dtflys.forest.annotation.OptionsRequest;
import com.dtflys.forest.annotation.ParamLifeCycle;
import com.dtflys.forest.annotation.Patch;
import com.dtflys.forest.annotation.PatchRequest;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.PostRequest;
import com.dtflys.forest.annotation.ProtobufBody;
import com.dtflys.forest.annotation.Put;
import com.dtflys.forest.annotation.PutRequest;
import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.Retry;
import com.dtflys.forest.annotation.Retryer;
import com.dtflys.forest.annotation.Return;
import com.dtflys.forest.annotation.SSLHostnameVerifier;
import com.dtflys.forest.annotation.SSLSocketFactoryBuilder;
import com.dtflys.forest.annotation.Success;
import com.dtflys.forest.annotation.Trace;
import com.dtflys.forest.annotation.TraceRequest;
import com.dtflys.forest.annotation.URLEncode;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.annotation.XMLBody;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.HttpBackendSelector;
import com.dtflys.forest.callback.AddressSource;
import com.dtflys.forest.callback.RetryWhen;
import com.dtflys.forest.callback.SuccessWhen;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.auto.DefaultAutoConverter;
import com.dtflys.forest.converter.binary.DefaultBinaryConverter;
import com.dtflys.forest.converter.form.DefaultFormConvertor;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.converter.json.JSONConverterSelector;
import com.dtflys.forest.converter.protobuf.ForestProtobufConverter;
import com.dtflys.forest.converter.protobuf.ForestProtobufConverterManager;
import com.dtflys.forest.converter.text.DefaultTextConverter;
import com.dtflys.forest.converter.xml.ForestXmlConverter;
import com.dtflys.forest.exceptions.ForestInterceptorDefineException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.filter.EncodeFilter;
import com.dtflys.forest.filter.EncodeFormFilter;
import com.dtflys.forest.filter.EncodePathFilter;
import com.dtflys.forest.filter.EncodeQueryFilter;
import com.dtflys.forest.filter.EncodeUserInfoFilter;
import com.dtflys.forest.filter.Filter;
import com.dtflys.forest.filter.JSONFilter;
import com.dtflys.forest.filter.XmlFilter;
import com.dtflys.forest.http.ForestAddress;
import com.dtflys.forest.http.ForestAsyncMode;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestType;
import com.dtflys.forest.interceptor.DefaultInterceptorFactory;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.interceptor.InterceptorFactory;
import com.dtflys.forest.lifecycles.BaseAnnotationLifeCycle;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.lifecycles.logging.LogEnabledLifeCycle;
import com.dtflys.forest.lifecycles.logging.LogHandlerLifeCycle;
import com.dtflys.forest.lifecycles.method.AddressLifeCycle;
import com.dtflys.forest.lifecycles.method.BackendClientLifeCycle;
import com.dtflys.forest.lifecycles.method.BackendLifeCycle;
import com.dtflys.forest.lifecycles.method.BodyTypeLifeCycle;
import com.dtflys.forest.lifecycles.method.DecompressGzipLifeCycle;
import com.dtflys.forest.lifecycles.method.DeleteRequestLifeCycle;
import com.dtflys.forest.lifecycles.method.GetRequestLifeCycle;
import com.dtflys.forest.lifecycles.method.HeadRequestLifeCycle;
import com.dtflys.forest.lifecycles.method.HeadersLifeCycle;
import com.dtflys.forest.lifecycles.method.OptionsRequestLifeCycle;
import com.dtflys.forest.lifecycles.method.PatchRequestLifeCycle;
import com.dtflys.forest.lifecycles.method.PostRequestLifeCycle;
import com.dtflys.forest.lifecycles.method.PutRequestLifeCycle;
import com.dtflys.forest.lifecycles.method.RequestLifeCycle;
import com.dtflys.forest.lifecycles.method.RetryLifeCycle;
import com.dtflys.forest.lifecycles.method.RetryerLifeCycle;
import com.dtflys.forest.lifecycles.method.SSLHostnameVerifierLifeCycle;
import com.dtflys.forest.lifecycles.method.SSLSocketFactoryBuilderLifeCycle;
import com.dtflys.forest.lifecycles.method.SuccessLifeCycle;
import com.dtflys.forest.lifecycles.method.TraceRequestLifeCycle;
import com.dtflys.forest.lifecycles.parameter.BinaryBodyLifeCycle;
import com.dtflys.forest.lifecycles.parameter.BodyLifeCycle;
import com.dtflys.forest.lifecycles.parameter.DataFileLifeCycle;
import com.dtflys.forest.lifecycles.parameter.FormBodyLifeCycle;
import com.dtflys.forest.lifecycles.parameter.HeaderLifeCycle;
import com.dtflys.forest.lifecycles.parameter.JSONBodyLifeCycle;
import com.dtflys.forest.lifecycles.parameter.JSONQueryLifeCycle;
import com.dtflys.forest.lifecycles.parameter.ProtobufBodyLifeCycle;
import com.dtflys.forest.lifecycles.parameter.QueryLifeCycle;
import com.dtflys.forest.lifecycles.parameter.ReturnLifeCycle;
import com.dtflys.forest.lifecycles.parameter.URLEncodeLifeCycle;
import com.dtflys.forest.lifecycles.parameter.VariableLifeCycle;
import com.dtflys.forest.lifecycles.parameter.XMLBodyLifeCycle;
import com.dtflys.forest.lifecycles.proxy.HTTPProxyLifeCycle;
import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.ForestLogHandler;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.mapping.ForestRequestContext;
import com.dtflys.forest.mapping.ForestVariableContext;
import com.dtflys.forest.pool.FixedRequestPool;
import com.dtflys.forest.pool.ForestRequestPool;
import com.dtflys.forest.proxy.ProxyFactory;
import com.dtflys.forest.reflection.DefaultObjectFactory;
import com.dtflys.forest.reflection.ForestObjectFactory;
import com.dtflys.forest.reflection.ForestVariableDef;
import com.dtflys.forest.reflection.MethodLifeCycleHandler;
import com.dtflys.forest.retryer.BackOffRetryer;
import com.dtflys.forest.ssl.SSLKeyStore;
import com.dtflys.forest.utils.ForestCache;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.TimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * global configuration
 * Forest全局配置管理类
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-03-24
 */
public class ForestConfiguration implements Serializable {

    private static Logger log = LoggerFactory.getLogger(ForestConfiguration.class);

    private final static Map<String, ForestConfiguration> CONFIGURATION_CACHE = new ConcurrentHashMap<>();

    private final Map<Class<?>, ProxyFactory<?>> CLIENT_PROXY_FACTORY_CACHE = new ConcurrentHashMap<>();

    private final Map<Class<?>, Object> CLIENT_CACHE = new ConcurrentHashMap<>(512);

    /**
     * 默认配置，再用户没有定义任何全局配置时使用该默认配置
     */
    private final static ForestConfiguration defaultConfiguration = configuration();

    /**
     * 请求接口的实例缓存，用于缓存请求接口的动态代理的实例
     */
    private Map<Class<?>, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * Forest 注解注册表
     *
     * @since 1.6.0
     */
    private Map<Class<? extends Annotation>, Class<? extends Interceptor>> annotationRegistration = new HashMap<>();

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
     * 最大请求等待队列大小
     */
    private Integer maxRequestQueueSize;

    /**
     * 最大异步线程池大小
     */
    private Integer maxAsyncThreadSize;

    /**
     * 异步模式
     */
    private ForestAsyncMode asyncMode = ForestAsyncMode.PLATFORM;

    /**
     * 最大异步线程池队列大小
     */
    private Integer maxAsyncQueueSize;

    /**
     * 是否自动重定向开关
     */
    private boolean autoRedirection = true;

    /**
     * 全局的请求超时时间，单位为毫秒
     */
    private Integer timeout;

    /**
     * 全局的请求数据字符集
     */
    private String charset = "UTF-8";

    /**
     * 全局的请求连接超时时间，单位为毫秒
     */
    private Integer connectTimeout;

    /**
     * 全局的请求读取超时时间，单位为毫秒
     */
    private Integer readTimeout;


    /**
     * 全局的请求失败重试策略类
     */
    private Class retryer;

    /**
     * 全局的最大请求失败重试次数
     */
    private Integer maxRetryCount;

    /**
     * 全局的最大请求重试之间的时间间隔，单位为毫秒
     */
    private long maxRetryInterval;

    /**
     * 全局默认地址(主机名/域名/ip地址 + 端口号)
     */
    private ForestAddress baseAddress;

    /**
     * 全局默认的主机地址信息动态来源接口实现类
     */
    private Class<? extends AddressSource> baseAddressSourceClass;

    /**
     * 全局的单向HTTPS请求的SSL协议，默认为 TLSv1.2
     */
    private String sslProtocol;

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
     * 后端客户端对象缓存最大空间大小
     */
    private Integer backendClientCacheMaxSize;


    /**
     * 后端客户端对象缓存过期时间
     */
    private Duration backendClientCacheExpireTime;

    /**
     * 后端客户端对象缓存
     */
    private ForestCache<String, Object> backendClientCache;


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
     * 异步线程池拒绝策略类型
     */
    private Class<? extends RejectedExecutionHandler> asyncRejectPolicyClass;


    /**
     * 全局请求成功条件回调函数类
     */
    private Class<? extends SuccessWhen> successWhenClass;

    /**
     * 全局重试条件回调函数
     */
    private Class<? extends RetryWhen> retryWhenClass;

    /**
     * 全局拦截器列表
     */
    private List<Class<? extends Interceptor>> interceptors;

    /**
     * 全局数据转换器表
     */
    private Map<ForestDataType, ForestConverter> converterMap;

    /**
     * 全局JSON数据转换器选择器
     */
    private JSONConverterSelector jsonConverterSelector;

    /**
     * Forest对象实例化工厂
     */
    private ForestObjectFactory forestObjectFactory;

    /**
     * 拦截器工厂
     */
    private InterceptorFactory interceptorFactory;

    /**
     * Properties配置属性
     */
    private ForestProperties properties;

    /**
     * HTTP后端选择器
     */
    private HttpBackendSelector httpBackendSelector = new HttpBackendSelector();

    /**
     * 全局过滤器注册表
     */
    private Map<String, Class<?>> filterRegisterMap = new HashMap<>();

    /**
     * 全局变量表
     */
    private ForestVariableContext variableContext = new ForestVariableContext(null, this);

    /**
     * SSL的Key Store集合
     */
    private Map<String, SSLKeyStore> sslKeyStores = new HashMap<>();

    /**
     * Forest请求池
     */
    private ForestRequestPool pool;


    /**
     * Forest异步请求线程池
     *
     * @since 1.5.29
     */
    ThreadPoolExecutor asyncPool;


    /**
     * Forest异步请求线程池同步锁
     */
    final Object ASYNC_POOL_LOCK = new Object();


    private ForestConfiguration() {
    }


    public static ForestConfiguration getDefaultConfiguration() {
        return defaultConfiguration;
    }

    /**
     * 实例化ForestConfiguration对象，并初始化默认值
     *
     * @return 新创建的ForestConfiguration实例
     */
    public static ForestConfiguration configuration() {
        return configuration("forestConfiguration");
    }

    /**
     * 实例化ForestConfiguration对象，并初始化默认值
     *
     * @param id 配置ID
     * @return 新创建的ForestConfiguration实例
     */
    public static ForestConfiguration configuration(final String id) {
        return CONFIGURATION_CACHE.computeIfAbsent(id, key ->
                createConfiguration().setId(key));
    }


    public static ForestConfiguration createConfiguration() {
        final ForestConfiguration configuration = new ForestConfiguration();
        configuration.setId("forestConfiguration" + configuration.hashCode());
        configuration.setJsonConverterSelector(new JSONConverterSelector());
        final ForestProtobufConverterManager protobufConverterFactory = ForestProtobufConverterManager.getInstance();
        configuration.setProtobufConverter(protobufConverterFactory.getForestProtobufConverter());
        ServiceLoader.load(ForestXmlConverter.class).forEach(configuration::setXmlConverter);
        configuration.setTextConverter(new DefaultTextConverter());
        DefaultAutoConverter autoConverter = new DefaultAutoConverter(configuration);
        final Map<ForestDataType, ForestConverter> converterMap = configuration.getConverterMap();
        converterMap.put(ForestDataType.AUTO, autoConverter);
        converterMap.put(ForestDataType.BINARY, new DefaultBinaryConverter(autoConverter));
        converterMap.put(ForestDataType.FORM, new DefaultFormConvertor(configuration));
        setupJSONConverter(configuration);
        configuration.setBackendClientCacheMaxSize(128);
        configuration.setTimeout(3000);
        configuration.setMaxConnections(500);
        configuration.setMaxRouteConnections(500);
        configuration.setRetryer(BackOffRetryer.class);
        configuration.setMaxRetryCount(0);
        configuration.setMaxRetryInterval(0);
        configuration.registerFilter("json", JSONFilter.class);
        configuration.registerFilter("xml", XmlFilter.class);
        configuration.registerFilter("encode", EncodeFilter.class);
        configuration.registerFilter("encodeUserInfo", EncodeUserInfoFilter.class);
        configuration.registerFilter("encodePath", EncodePathFilter.class);
        configuration.registerFilter("encodeQuery", EncodeQueryFilter.class);
        configuration.registerFilter("encodeForm", EncodeFormFilter.class);
        configuration.setLogHandler(new DefaultLogHandler());

        // 注册基本请求注解
        configuration.registerAnnotation(Request.class, RequestLifeCycle.class);
        configuration.registerAnnotation(Get.class, GetRequestLifeCycle.class);
        configuration.registerAnnotation(GetRequest.class, GetRequestLifeCycle.class);
        configuration.registerAnnotation(Post.class, PostRequestLifeCycle.class);
        configuration.registerAnnotation(PostRequest.class, PostRequestLifeCycle.class);
        configuration.registerAnnotation(Put.class, PutRequestLifeCycle.class);
        configuration.registerAnnotation(PutRequest.class, PutRequestLifeCycle.class);
        configuration.registerAnnotation(Delete.class, DeleteRequestLifeCycle.class);
        configuration.registerAnnotation(DeleteRequest.class, DeleteRequestLifeCycle.class);
        configuration.registerAnnotation(HeadRequest.class, HeadRequestLifeCycle.class);
        configuration.registerAnnotation(Options.class, OptionsRequestLifeCycle.class);
        configuration.registerAnnotation(OptionsRequest.class, OptionsRequestLifeCycle.class);
        configuration.registerAnnotation(Patch.class, PatchRequestLifeCycle.class);
        configuration.registerAnnotation(PatchRequest.class, PatchRequestLifeCycle.class);
        configuration.registerAnnotation(Trace.class, TraceRequestLifeCycle.class);
        configuration.registerAnnotation(TraceRequest.class, TraceRequestLifeCycle.class);

        // 注册方法注解
        configuration.registerAnnotation(Address.class, AddressLifeCycle.class);
        configuration.registerAnnotation(Backend.class, BackendLifeCycle.class);
        configuration.registerAnnotation(BackendClient.class, BackendClientLifeCycle.class);
        configuration.registerAnnotation(BodyType.class, BodyTypeLifeCycle.class);
        configuration.registerAnnotation(DecompressGzip.class, DecompressGzipLifeCycle.class);
        configuration.registerAnnotation(Headers.class, HeadersLifeCycle.class);
        configuration.registerAnnotation(HTTPProxy.class, HTTPProxyLifeCycle.class);
        configuration.registerAnnotation(LogEnabled.class, LogEnabledLifeCycle.class);
        configuration.registerAnnotation(LogHandler.class, LogHandlerLifeCycle.class);
        configuration.registerAnnotation(SSLHostnameVerifier.class, SSLHostnameVerifierLifeCycle.class);
        configuration.registerAnnotation(SSLSocketFactoryBuilder.class, SSLSocketFactoryBuilderLifeCycle.class);
        configuration.registerAnnotation(Retry.class, RetryLifeCycle.class);
        configuration.registerAnnotation(Retryer.class, RetryerLifeCycle.class);
        configuration.registerAnnotation(Success.class, SuccessLifeCycle.class);
        configuration.registerAnnotation(Return.class, ReturnLifeCycle.class);

        // 注册参数注解
        configuration.registerAnnotation(Query.class, QueryLifeCycle.class);
        configuration.registerAnnotation(JSONQuery.class, JSONQueryLifeCycle.class);
        configuration.registerAnnotation(Body.class, BodyLifeCycle.class);
        configuration.registerAnnotation(FormBody.class, FormBodyLifeCycle.class);
        configuration.registerAnnotation(JSONBody.class, JSONBodyLifeCycle.class);
        configuration.registerAnnotation(XMLBody.class, XMLBodyLifeCycle.class);
        configuration.registerAnnotation(BinaryBody.class, BinaryBodyLifeCycle.class);
        configuration.registerAnnotation(ProtobufBody.class, ProtobufBodyLifeCycle.class);
        configuration.registerAnnotation(FileBody.class, DataFileLifeCycle.class);
        configuration.registerAnnotation(Header.class, HeaderLifeCycle.class);
        configuration.registerAnnotation(Var.class, VariableLifeCycle.class);
        configuration.registerAnnotation(URLEncode.class, URLEncodeLifeCycle.class);
        return configuration;
    }

    /**
     * 获取所有定义过的 Forest 配置信息
     *
     * @return Forest 配置信息列表
     */
    public List<ForestConfiguration> allConfigurations() {
        return CONFIGURATION_CACHE.values().stream().collect(Collectors.toList());
    }


    /**
     * 获取请求接口实例缓存，返回的缓存对象集合用于缓存请求接口的动态代理的实例
     *
     * @return 缓存对象集合
     */
    public Map<Class<?>, Object> getInstanceCache() {
        return instanceCache;
    }

    /**
     * 配置HTTP后端
     *
     * @return 当前ForestConfiguration实例
     */
    private ForestConfiguration setupBackend() {
        setBackend(httpBackendSelector.select(this));
        return this;
    }

    /**
     * 获取Forest后端框架选择器
     *
     * @return Forest后端框架选择器
     */
    public HttpBackendSelector getBackendSelector() {
        return httpBackendSelector;
    }

    /**
     * 设置HTTP后端
     *
     * @param backend HTTP后端对象
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setBackend(final HttpBackend backend) {
        if (backend != null) {
            backend.init(this);
            log.info("[Forest] Http Backend: " + backend.getName());
        }
        this.backend = backend;
        return this;
    }

    /**
     * 设置HTTP后端名称
     *
     * @param backendName HTTP后端名称
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setBackendName(final String backendName) {
        this.backendName = backendName;
        return this;
    }

    /**
     * 获取HTTP后端名称
     *
     * @return HTTP后端名称
     */
    public String getBackendName() {
        if (backend != null) {
            return backend.getName();
        }
        return backendName;
    }

    /**
     * 获取当前HTTP后端
     *
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
     * 获取所有已创建的Forest后端框架
     *
     * @return Map实例，Key: 后端框架名称, Value: {@link HttpBackend}接口实例
     */
    public Map<String, HttpBackend> getAllCreatedBackends() {
        return httpBackendSelector.getAllCreatedBackends();
    }

    /**
     * 获取后端客户端对象缓存最大空间大小
     *
     * @return 后端客户端对象缓存最大空间大小
     * @since 1.5.34
     */
    public Integer getBackendClientCacheMaxSize() {
        return backendClientCacheMaxSize;
    }


    /**
     * 设置后端客户端对象缓存最大空间大小
     *
     * @param backendClientCacheMaxSize 后端客户端对象缓存最大空间大小
     * @return 当前ForestConfiguration实例
     * @since 1.5.34
     */
    public ForestConfiguration setBackendClientCacheMaxSize(final Integer backendClientCacheMaxSize) {
        this.backendClientCacheMaxSize = backendClientCacheMaxSize;
        return this;
    }

    /**
     * 获取后端客户端对象缓存过期时间
     *
     * @return 后端客户端对象缓存过期时间
     * @since 1.5.35
     */
    public Duration getBackendClientCacheExpireTime() {
        return backendClientCacheExpireTime;
    }

    /**
     * 设置获取后端客户端对象缓存过期时间
     *
     * @param backendClientCacheExpireTime 后端客户端对象缓存过期时间
     * @return 当前ForestConfiguration实例
     * @since 1.5.35
     */
    public ForestConfiguration setBackendClientCacheExpireTime(final Duration backendClientCacheExpireTime) {
        this.backendClientCacheExpireTime = backendClientCacheExpireTime;
        return this;
    }

    /**
     * 获取Forest对象实例化工厂
     *
     * @return Forest对象实例化工厂对象
     */
    public ForestObjectFactory getForestObjectFactory() {
        if (forestObjectFactory == null) {
            synchronized (this) {
                if (forestObjectFactory == null) {
                    forestObjectFactory = new DefaultObjectFactory();
                }
            }
        }
        return forestObjectFactory;
    }

    /**
     * 获取Forest接口对象
     * <p>适用于Forest相关接口(非请求客户端接口)和回调函数的工厂接口
     * <p>当这些类没有实例的情况下，会先实例化并缓存下来，以后再取会通过缓存获取对象
     *
     * @param clazz Forest对象接口类
     * @param <T>   Forest对象接口类泛型
     * @return Forest对象实例
     */
    public <T> T getForestObject(final Class<T> clazz) {
        return getForestObjectFactory().getObject(clazz);
    }


    /**
     * 设置Forest对象实例化工厂
     *
     * @param forestObjectFactory Forest对象实例化工厂对象
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setForestObjectFactory(final ForestObjectFactory forestObjectFactory) {
        this.forestObjectFactory = forestObjectFactory;
        return this;
    }

    /**
     * 获取拦截器工厂
     *
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
     *
     * @param interceptorFactory 拦截器工厂
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setInterceptorFactory(final InterceptorFactory interceptorFactory) {
        this.interceptorFactory = interceptorFactory;
        return this;
    }

    /**
     * 获取Properties配置属性
     *
     * @return {@link ForestProperties}类实例
     */
    public ForestProperties getProperties() {
        if (properties == null) {
            synchronized (this) {
                if (properties == null) {
                    properties = new ForestProperties();
                }
            }
        }
        return properties;
    }

    /**
     * 设置Properties配置属性
     *
     * @param properties {@link ForestProperties}类实例
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setProperties(final ForestProperties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * 设置HTTP后端选择器
     *
     * @param httpBackendSelector HTTP后端选择器
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setHttpBackendSelector(final HttpBackendSelector httpBackendSelector) {
        this.httpBackendSelector = httpBackendSelector;
        return this;
    }

    /**
     * 配置JSON数据转换器
     *
     * @param configuration 全局配置对象
     */
    private static void setupJSONConverter(final ForestConfiguration configuration) {
        configuration.setJsonConverter(configuration.jsonConverterSelector.select());
    }

    /**
     * 获取全局配置ID
     *
     * @return 全局配置ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置全局配置ID
     *
     * @param id 全局配置ID
     * @return 当前ForestConfiguration实例
     */
    private ForestConfiguration setId(final String id) {
        this.id = id;
        return this;
    }

    /**
     * 获取全局的最大连接数
     *
     * @return 最大连接数
     */
    public Integer getMaxConnections() {
        return maxConnections;
    }

    /**
     * 设置全局的最大连接数
     *
     * @param maxConnections 全局的最大连接数
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setMaxConnections(final Integer maxConnections) {
        this.maxConnections = maxConnections;
        return this;
    }

    /**
     * 获取全局的最大请求路径连接数
     *
     * @return 最大请求路径连接数
     */
    public Integer getMaxRouteConnections() {
        return maxRouteConnections;
    }

    /**
     * 设置全局的最大请求路径连接数
     *
     * @param maxRouteConnections 最大请求路径连接数
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setMaxRouteConnections(final Integer maxRouteConnections) {
        this.maxRouteConnections = maxRouteConnections;
        return this;
    }

    /**
     * 获取全局的最大请求等待队列大小
     *
     * @return 最大请求等待队列大小
     * @author dt_flys@hotmail.com
     * @since 1.5.22
     */
    public Integer getMaxRequestQueueSize() {
        return maxRequestQueueSize;
    }

    /**
     * 设置全局的最大请求等待队列大小
     *
     * @param maxRequestQueueSize 最大请求等待队列大小
     * @return 当前ForestConfiguration实例
     * @author dt_flys@hotmail.com
     * @since 1.5.22
     */
    public ForestConfiguration setMaxRequestQueueSize(final Integer maxRequestQueueSize) {
        this.maxRequestQueueSize = maxRequestQueueSize;
        return this;
    }

    /**
     * 获取最大异步线程池大小
     *
     * @return 最大异步线程池大小
     */
    public Integer getMaxAsyncThreadSize() {
        return maxAsyncThreadSize;
    }

    /**
     * 设置最大异步线程池大小
     *
     * @param maxAsyncThreadSize 最大异步线程池大小
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setMaxAsyncThreadSize(final Integer maxAsyncThreadSize) {
        this.maxAsyncThreadSize = maxAsyncThreadSize;
        return this;
    }

    /**
     * 获取异步模式
     *
     * @return {@link ForestAsyncMode}枚举实例
     * @since 1.5.27
     */
    public ForestAsyncMode getAsyncMode() {
        return asyncMode;
    }

    /**
     * 设置异步模式
     *
     * @param asyncMode 异步模式 - {@link ForestAsyncMode}枚举实例
     * @return 当前ForestConfiguration实例
     * @since 1.5.27
     */
    public ForestConfiguration setAsyncMode(final ForestAsyncMode asyncMode) {
        this.asyncMode = asyncMode;
        return this;
    }

    /**
     * 获取最大异步线程池队列大小
     *
     * @return 最大异步线程池队列大小
     * @author dt_flys@hotmail.com
     * @since 1.5.22
     */
    public Integer getMaxAsyncQueueSize() {
        return maxAsyncQueueSize;
    }

    /**
     * 设置最大异步线程池队列大小
     *
     * @param maxAsyncQueueSize 最大异步线程池队列大小
     * @return 当前ForestConfiguration实例
     * @author dt_flys@hotmail.com
     * @since 1.5.22
     */
    public ForestConfiguration setMaxAsyncQueueSize(final Integer maxAsyncQueueSize) {
        this.maxAsyncQueueSize = maxAsyncQueueSize;
        return this;
    }

    /**
     * 是否自动重定向开关
     *
     * @return {@code true}: 打开, {@code false}: 禁止
     */
    public boolean isAutoRedirection() {
        return autoRedirection;
    }

    /**
     * /**
     * 设置是否打开自动重定向
     *
     * @param autoRedirection {@code true}: 打开, {@code false}: 禁止
     * @return {@link ForestRequest}类实例
     */
    public ForestConfiguration setAutoRedirection(final boolean autoRedirection) {
        this.autoRedirection = autoRedirection;
        return this;
    }

    /**
     * 获取全局的请求超时时间，单位为毫秒
     *
     * @return 请求超时时间，单位为毫秒
     */
    @Deprecated
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * 设置全局的请求超时时间，单位为毫秒
     *
     * @param timeout 请求超时时间，单位为毫秒
     * @return 当前ForestConfiguration实例
     */
    @Deprecated
    public ForestConfiguration setTimeout(final Integer timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 获取全局的请求数据字符集
     *
     * @return 字符集名称
     */
    public String getCharset() {
        return charset;
    }

    /**
     * 设置全局的请求数据字符集
     *
     * @param charset 字符集名称
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setCharset(final String charset) {
        this.charset = charset;
        return this;
    }

    /**
     * 获取全局的请求连接超时时间，单位为毫秒
     *
     * @return 连接超时时间，单位为毫秒
     */
    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 设置全局的请求连接超时时间，单位为毫秒
     *
     * @param connectTimeout 连接超时时间，单位为毫秒
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setConnectTimeout(final Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * 设置全局的请求连接超时时间
     *
     * @param connectTimeout 连接超时时间, 整数
     * @param timeUnit       时间单位
     * @return 当前ForestConfiguration实例
     * @since 1.5.6
     */
    public ForestConfiguration setConnectTimeout(final int connectTimeout, final TimeUnit timeUnit) {
        this.connectTimeout = TimeUtils.toMillis("global connect timeout", connectTimeout, timeUnit);
        return this;
    }

    /**
     * 设置全局的请求连接超时时间
     *
     * @param connectTimeout 连接超时时间, {@link Duration}对象
     * @return 当前ForestConfiguration实例
     * @since 1.5.6
     */
    public ForestConfiguration setConnectTimeout(final Duration connectTimeout) {
        this.connectTimeout = TimeUtils.toMillis("global connect timeout", connectTimeout);
        return this;
    }


    /**
     * 获取全局的请求读取超时时间，单位为毫秒
     *
     * @return 读取超时时间
     * @since 1.5.6
     */
    public Integer getReadTimeout() {
        return readTimeout;
    }

    /**
     * 设置全局的请求读取超时时间，单位为毫秒
     *
     * @param readTimeout 读取超时时间，单位为毫秒
     * @return 当前ForestConfiguration实例
     * @since 1.5.6
     */
    public ForestConfiguration setReadTimeout(final Integer readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    /**
     * 设置全局的请求读取超时时间
     *
     * @param readTimeout 读取超时时间
     * @param timeUnit    时间单位
     * @return 当前ForestConfiguration实例
     * @since 1.5.6
     */
    public ForestConfiguration setReadTimeout(final int readTimeout, final TimeUnit timeUnit) {
        this.readTimeout = TimeUtils.toMillis("global read timeout", readTimeout, timeUnit);
        return this;
    }

    /**
     * 设置全局的请求读取超时时间
     *
     * @param readTimeout 读取超时时间, {@link Duration}
     * @return 当前ForestConfiguration实例
     * @since 1.5.6
     */
    public ForestConfiguration setReadTimeout(final Duration readTimeout) {
        this.readTimeout = TimeUtils.toMillis("global read timeout", readTimeout);
        return this;
    }


    /**
     * 获取全局的请求失败重试策略类
     *
     * @return 重试策略类
     */
    public Class getRetryer() {
        return retryer;
    }

    /**
     * 设置全局的请求失败重试策略类
     *
     * @param retryer 重试策略类
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setRetryer(final Class retryer) {
        this.retryer = retryer;
        return this;
    }

    /**
     * 获取全局的最大请求失败重试次数
     *
     * @return 重试次数
     */
    @Deprecated
    public Integer getRetryCount() {
        return maxRetryCount;
    }

    /**
     * 设置全局的最大请求失败重试次数
     *
     * @param retryCount 重试次数
     * @return 当前ForestConfiguration实例
     */
    @Deprecated
    public ForestConfiguration setRetryCount(final Integer retryCount) {
        this.maxRetryCount = retryCount;
        return this;
    }

    /**
     * 获取全局的最大请求失败重试次数
     *
     * @return 重试次数
     */
    public Integer getMaxRetryCount() {
        return maxRetryCount;
    }


    /**
     * 设置全局的最大请求失败重试次数
     *
     * @param maxRetryCount 最大重试次数
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setMaxRetryCount(final Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
        return this;
    }


    /**
     * 获取全局的最大请求重试之间的时间间隔，单位为毫秒
     *
     * @return 最大请求重试之间的时间间隔
     */
    public long getMaxRetryInterval() {
        return maxRetryInterval;
    }

    /**
     * 设置全局的最大请求重试之间的时间间隔，单位为毫秒
     *
     * @param maxRetryInterval 最大请求重试之间的时间间隔
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setMaxRetryInterval(final long maxRetryInterval) {
        this.maxRetryInterval = maxRetryInterval;
        return this;
    }

    /**
     * 获取全局默认地址(主机名/域名/ip地址 + 端口号)
     *
     * @return 全局默认地址 {@link ForestAddress}对象
     */
    public ForestAddress getBaseAddress() {
        return baseAddress;
    }

    /**
     * 设置全局默认地址(主机名/域名/ip地址 + 端口号)
     *
     * @param baseAddress 全局默认地址 {@link ForestAddress}对象
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setBaseAddress(final ForestAddress baseAddress) {
        this.baseAddress = baseAddress;
        return this;
    }

    /**
     * 设置全局地址的HTTP协议头
     *
     * @param scheme HTTP 协议头
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setBaseAddressScheme(final String scheme) {
        if (baseAddress == null) {
            baseAddress = new ForestAddress(scheme, null, null);
        } else {
            baseAddress = new ForestAddress(scheme, baseAddress.getHost(), baseAddress.getPort());
        }
        return this;
    }


    /**
     * 设置全局地址的 host
     *
     * @param host 全局地址的主机名/ip地址
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setBaseAddressHost(final String host) {
        if (baseAddress == null) {
            baseAddress = new ForestAddress(host, null);
        } else {
            baseAddress = new ForestAddress(baseAddress.getScheme(), host, baseAddress.getPort());
        }
        return this;
    }

    /**
     * 设置全局地址的 port
     *
     * @param port 全局地址的端口号
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setBaseAddressPort(final Integer port) {
        if (port == null) {
            return this;
        }
        if (baseAddress == null) {
            baseAddress = new ForestAddress(null, port);
        } else {
            baseAddress = new ForestAddress(baseAddress.getScheme(), baseAddress.getHost(), port);
        }
        return this;
    }


    /**
     * 获取全局默认的主机地址信息动态来源
     *
     * @return {@link AddressSource}接口实例
     */
    public AddressSource getBaseAddressSource() {
        if (baseAddressSourceClass == null) {
            return null;
        }
        return getForestObjectFactory().getObject(baseAddressSourceClass);
    }

    /**
     * 设置全局默认的主机地址信息动态来源接口实现类
     *
     * @param baseAddressSourceClass {@link AddressSource}接口实现类
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setBaseAddressSourceClass(final Class<? extends AddressSource> baseAddressSourceClass) {
        this.baseAddressSourceClass = baseAddressSourceClass;
        return this;
    }

    /**
     * 获取全局的单向HTTPS请求的SSL协议，默认为 TLSv1.2
     *
     * @return SSL协议名称
     */
    public String getSslProtocol() {
        return sslProtocol;
    }

    /**
     * 设置全局的单向HTTPS请求的SSL协议
     *
     * @param sslProtocol SSL协议名称
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setSslProtocol(final String sslProtocol) {
        this.sslProtocol = sslProtocol;
        return this;
    }

    /**
     * 是否允许打印请求日志
     *
     * @return 允许为 {@code true} , 否则为 {@code false}
     */
    public boolean isLogEnabled() {
        return logEnabled;
    }

    /**
     * 设置是否允许打印请求日志
     *
     * @param logEnabled 允许为 {@code true} , 否则为 {@code false}
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setLogEnabled(final boolean logEnabled) {
        this.logEnabled = logEnabled;
        return this;
    }

    /**
     * 是否允许打印请求/响应日志
     *
     * @return 允许为 {@code true}, 否则为 {@code false}
     */
    public boolean isLogRequest() {
        return logRequest;
    }

    /**
     * 设置是否允许打印请求/响应日志
     *
     * @param logRequest 允许为 {@code true} , 否则为 {@code false}
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setLogRequest(final boolean logRequest) {
        this.logRequest = logRequest;
        return this;
    }

    /**
     * 是否允许打印响应日志
     *
     * @return 允许为 {@code true}, 否则为 {@code false}
     */
    public boolean isLogResponseStatus() {
        return logResponseStatus;
    }

    /**
     * 设置是否允许打印响应日志
     *
     * @param logResponseStatus 允许为 {@code true}, 否则为 {@code false}
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setLogResponseStatus(final boolean logResponseStatus) {
        this.logResponseStatus = logResponseStatus;
        return this;
    }

    /**
     * 是否允许打印响应日志
     *
     * @return 允许为 {@code true}, 否则为 {@code false}
     */
    public boolean isLogResponseContent() {
        return logResponseContent;
    }

    /**
     * 设置是否允许打印响应日志
     *
     * @param logResponseContent 允许为 {@code true}, 否则为 {@code false}
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setLogResponseContent(final boolean logResponseContent) {
        this.logResponseContent = logResponseContent;
        return this;
    }

    /**
     * 获取日志处理器
     *
     * @return 日志处理器接口实例
     */
    public ForestLogHandler getLogHandler() {
        return logHandler;
    }

    /**
     * 设置日志处理器
     *
     * @param logHandler 日志处理器接口实例
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setLogHandler(final ForestLogHandler logHandler) {
        this.logHandler = logHandler;
        return this;
    }

    /**
     * 是否缓存请求接口实例
     *
     * @return 如果允许缓存实例为 {@code true}, 否则为 {@code false}
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    /**
     * 设置是否缓存请求接口实例
     *
     * @param cacheEnabled 如果允许缓存实例为 {@code true}, 否则为 {@code false}
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setCacheEnabled(final boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
        return this;
    }

    /**
     * 获取全局默认请求参数列表
     *
     * @return {@link RequestNameValue} 对象列表
     */
    public List<RequestNameValue> getDefaultParameters() {
        return defaultParameters;
    }

    /**
     * 设置全局默认请求参数列表
     *
     * @param defaultParameters 请求参数列表
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setDefaultParameters(final List<RequestNameValue> defaultParameters) {
        this.defaultParameters = defaultParameters;
        return this;
    }

    /**
     * 获取全局默认请求头信息列表
     *
     * @return {@link RequestNameValue} 对象列表
     */
    public List<RequestNameValue> getDefaultHeaders() {
        return defaultHeaders;
    }

    /**
     * 设置全局默认请求头信息列表
     *
     * @param defaultHeaders 请求头信息列表
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setDefaultHeaders(final List<RequestNameValue> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    public RejectedExecutionHandler getAsyncRejectPolicy() {
        if (asyncRejectPolicyClass == null) {
            return getForestObjectFactory().getObject(RejectedExecutionHandler.class);
        }
        return getForestObjectFactory().getObject(asyncRejectPolicyClass);
    }

    /**
     * 获取异步线程池拒绝策略类型
     *
     * @return Class实例
     */
    public Class<? extends RejectedExecutionHandler> getAsyncRejectPolicyClass() {
        return asyncRejectPolicyClass;
    }

    /**
     * 设置异步线程池拒绝策略类型
     *
     * @param asyncRejectPolicyClass Class实例
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setAsyncRejectPolicyClass(final Class<? extends RejectedExecutionHandler> asyncRejectPolicyClass) {
        this.asyncRejectPolicyClass = asyncRejectPolicyClass;
        return this;
    }

    /**
     * 获取全局重试条件回调函数
     *
     * @return 重试条件回调函数 {@link RetryWhen} 实例
     */
    public RetryWhen getRetryWhen() {
        if (retryWhenClass == null) {
            return null;
        }
        return getForestObjectFactory().getObject(retryWhenClass);
    }

    /**
     * 设置全局重试条件回调函数
     *
     * @param retryWhenClass {@link RetryWhen}接口实现类
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setRetryWhenClass(final Class<? extends RetryWhen> retryWhenClass) {
        this.retryWhenClass = retryWhenClass;
        return this;
    }

    /**
     * 获取全局请求成功条件回调函数
     *
     * @return {@link SuccessWhen}接口实例
     */
    public SuccessWhen getSuccessWhen() {
        if (successWhenClass == null) {
            return null;
        }
        return getForestObjectFactory().getObject(successWhenClass);
    }

    /**
     * 设置全局请求成功条件回调函数
     *
     * @param successWhenClass {@link SuccessWhen}接口实现类
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setSuccessWhenClass(final Class<? extends SuccessWhen> successWhenClass) {
        this.successWhenClass = successWhenClass;
        return this;
    }

    /**
     * 获取全局拦截器列表
     *
     * @return {@link Class} 实例列表
     */
    public List<Class<? extends Interceptor>> getInterceptors() {
        return interceptors;
    }

    /**
     * 设置全局拦截器列表
     *
     * @param interceptors 全局拦截器列表
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setInterceptors(final List<Class<? extends Interceptor>> interceptors) {
        this.interceptors = interceptors;
        return this;
    }

    /**
     * 添加全局拦截器
     *
     * @param interceptorClass 局拦截器类
     * @return 当前ForestConfiguration实例
     * @since 2.0.0-BETA
     */
    public ForestConfiguration addInterceptor(final Class<? extends Interceptor> interceptorClass) {
        if (this.interceptors == null) {
            this.interceptors = new ArrayList<>();
        }
        this.interceptors.add(interceptorClass);
        return this;
    }

    /**
     * 设置全局JSON数据转换器
     *
     * @param converter JSON数据转换器
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setJsonConverter(final ForestJsonConverter converter) {
        getConverterMap().put(ForestDataType.JSON, converter);
        return this;
    }

    /**
     * 获取当前全局JSON数据转换器
     *
     * @return JSON数据转换器
     */
    public ForestJsonConverter getJsonConverter() {
        ForestJsonConverter jsonConverter = (ForestJsonConverter) getConverterMap().get(ForestDataType.JSON);
        if (jsonConverter == null) {
            throw new ForestRuntimeException("JSON converter cannot be found. Please check your classpath if there is the JSON framework, eg. Jackson (>= 2.9.10), Gson or Fastjson (>= 1.2.48), in the dependencies of your project.");
        }
        return jsonConverter;
    }

    /**
     * 设置全局XML数据转换器
     *
     * @param converter XML数据转换器
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setXmlConverter(final ForestXmlConverter converter) {
        getConverterMap().put(ForestDataType.XML, converter);
        return this;
    }

    /**
     * 获取当前全局XML数据转换器
     *
     * @return XML数据转换器
     */
    public ForestXmlConverter getXmlConverter() {
        return (ForestXmlConverter) getConverterMap().get(ForestDataType.XML);
    }

    /**
     * 设置全局XML数据转换器
     *
     * @param converter XML数据转换器
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setProtobufConverter(final ForestProtobufConverter converter) {
        getConverterMap().put(ForestDataType.PROTOBUF, converter);
        return this;
    }

    /**
     * 获取当前全局XML数据转换器
     *
     * @return XML数据转换器
     */
    public ForestProtobufConverter getProtobufConverter() {
        ForestProtobufConverter converter = (ForestProtobufConverter) getConverterMap().get(ForestDataType.PROTOBUF);
        if (converter == null) {
            throw new ForestRuntimeException("Protobuf converter cannot be found. Please check your classpath if there is the Protobuf framework in the dependencies of your project.");
        }

        return converter;
    }


    /**
     * 设置全局文本数据转换器
     *
     * @return 当前ForestConfiguration实例
     */
    private ForestConfiguration setTextConverter(final ForestConverter converter) {
        getConverterMap().put(ForestDataType.TEXT, converter);
        return this;
    }

    /**
     * 获取全局文本数据转换器
     *
     * @return 当前ForestConfiguration实例
     */
    private ForestConverter getTextConverter() {
        return getConverterMap().get(ForestDataType.TEXT);
    }


    /**
     * 根据请求接口类创建并获取请求接口的动态代理工厂
     *
     * @param clazz 请求接口类
     * @param <T>   请求接口类泛型
     * @return 动态代理工厂
     */
    public <T> ProxyFactory<T> getProxyFactory(final Class<T> clazz) {
        return (ProxyFactory<T>) CLIENT_PROXY_FACTORY_CACHE
                .computeIfAbsent(clazz, cls -> new ProxyFactory<T>(this, (Class<T>) cls));
    }




    /**
     * 设置全局变量
     *
     * @param name  变量名
     * @param value 变量值
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setVar(final String name, final Object value) {
        this.variableContext.setVar(name, value);
        return this;
    }

    /**
     * 设置全局变量
     *
     * @param name  变量名
     * @param value {@link ForestVariableDef}类型变量值
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setVar(final String name, final ForestVariableDef value) {
        this.variableContext.setVar(name, value);
        return this;
    }

    /**
     * 根据变量名获取全局变量值
     *
     * @param name 变量名
     * @return 变量值
     */
    public Object getVar(final String name) {
        return getVar(name, new ForestRequestContext(null, new Object[0]));
    }

    public Object getVar(final String name, final VariableValueContext valueContext) {
        return this.variableContext.getVar(name, valueContext);
    }


    /**
     * 判断变量是否已定义
     *
     * @param name 变量名
     * @return {@code true}: 已定义, {@code false}: 未定义
     */
    public boolean isVarDefined(final String name) {
        return variableContext.isVarDefined(name);
    }

    /**
     * 获取全局SSL的Key Store表
     *
     * @return SSL的Key Store表
     */
    public Map<String, SSLKeyStore> getSslKeyStores() {
        return sslKeyStores;
    }

    /**
     * 设置全局SSL的Key Store表
     *
     * @param sslKeyStores SSL的Key Store表
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setSslKeyStores(final Map<String, SSLKeyStore> sslKeyStores) {
        this.sslKeyStores = sslKeyStores;
        return this;
    }

    /**
     * 获取全局请求池
     *
     * @return Forest请求池
     */
    public ForestRequestPool getPool() {
        if (pool == null) {
            synchronized (this) {
                if (pool == null) {
                    pool = new FixedRequestPool(this);
                }
            }
        }
        return pool;
    }

    /**
     * 设置全局请求池
     *
     * @param pool Forest请求池
     */
    public void setPool(final ForestRequestPool pool) {
        this.pool = pool;
    }

    /**
     * 获取后端客户端对象缓存
     *
     * @return 后端客户端对象缓存
     * @since 1.5.34
     */
    public ForestCache<String, Object> getBackendClientCache() {
        if (backendClientCache == null) {
            synchronized (this) {
                if (backendClientCache == null) {
                    final Duration expireTime = getBackendClientCacheExpireTime();
                    if (expireTime != null) {
                        backendClientCache = new ForestCache<>(getBackendClientCacheMaxSize(), expireTime.getNano(), TimeUnit.NANOSECONDS);
                    } else {
                        backendClientCache = new ForestCache<>(getBackendClientCacheMaxSize(), 6, TimeUnit.HOURS);
                    }
                }
            }
        }
        return backendClientCache;
    }


    /**
     * 从缓存中获取后端客户端对象
     *
     * @param key 缓存的Key
     * @param <T> 后端客户端对象类型
     * @return 后端客户端对象
     */
    public <T> T getBackendClient(final String key) {
        final Object client = getBackendClientCache().get(key);
        if (client != null) {
            return (T) client;
        }
        return null;
    }

    /**
     * 缓存后端客户端对象
     *
     * @param key 缓存的Key
     * @param client 后端客户端对象
     */
    public void putBackendClientToCache(final String key, final Object client) {
        getBackendClientCache().put(key, client);
    }



    /**
     * 注册全局SSL的Key Store信息
     *
     * @param keyStore Key Store信息
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration registerKeyStore(final SSLKeyStore keyStore) {
        sslKeyStores.put(keyStore.getId(), keyStore);
        return this;
    }

    /**
     * 根据ID获取全局SSL的Key Store信息
     *
     * @param id Key Store的ID
     * @return {@link SSLKeyStore} 实例
     */
    public SSLKeyStore getKeyStore(final String id) {
        return sslKeyStores.get(id);
    }

    /**
     * 根据请求响应数据类型获取数据转换器
     *
     * @param dataType 请求响应数据类型
     * @return 数据转换器
     */
    public ForestConverter getConverter(final ForestDataType dataType) {
        ForestConverter converter = getConverterMap().get(dataType);
        if (converter == null) {
            throw new ForestRuntimeException("Can not found converter for type " + dataType.getName());
        }
        return converter;
    }

    /**
     * 获取全局数据转换器表
     *
     * @return 数据转换器表
     */
    public Map<ForestDataType, ForestConverter> getConverterMap() {
        if (converterMap == null) {
            converterMap = new HashMap<>();
        }
        return converterMap;
    }

    /**
     * 设置全局数据转换器表
     *
     * @param converterMap 数据转换器表
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setConverterMap(final Map<ForestDataType, ForestConverter> converterMap) {
        this.converterMap = converterMap;
        return this;
    }

    /**
     * 设置合并全局数据转换器表，但不会覆盖整个转换器表
     *
     * @param converterMap 数据转换器表
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration setToMergeConverterMap(final Map<ForestDataType, ForestConverter> converterMap) {
        if (converterMap == null) {
            return this;
        }
        for (Map.Entry<ForestDataType, ForestConverter> entry : converterMap.entrySet()) {
            ForestDataType dataType = entry.getKey();
            ForestConverter converter = entry.getValue();
            this.converterMap.put(dataType, converter);
        }
        return this;
    }



    public ForestVariableContext getVariableContext() {
        return variableContext;
    }


    /**
     * 创建请求接口的动态代理实例
     *
     * @param clazz 请求接口类
     * @param <T>   请求接口类泛型
     * @return 动态代理实例
     * @see ForestConfiguration#client(Class)
     */
    public <T> T createInstance(final Class<T> clazz) {
        ProxyFactory<T> proxyFactory = getProxyFactory(clazz);
        return proxyFactory.createInstance();
    }

    /**
     * 注册 Forest 注解
     *
     * @param annotationClass 注解类
     * @param interceptorClass 拦截器类
     * @return {@link ForestConfiguration}实例
     * @since 1.6.0
     */
    public ForestConfiguration registerAnnotation(final Class<? extends Annotation> annotationClass, final Class<? extends Interceptor> interceptorClass) {
        if (annotationRegistration.containsKey(annotationClass)) {
            throw new ForestRuntimeException("Annotation type \"" + annotationClass.getName() + "\" has been already registered");
        }
        annotationRegistration.put(annotationClass, interceptorClass);
        return this;
    }

    /**
     * 获取 Forest 注解所注册的拦截器类
     *
     * @param annotationClass 注解类
     * @return 拦截器类
     * @since 1.6.0
     */
    public Class<? extends Interceptor> getInterceptorClassFromAnnotation(final Class<? extends Annotation> annotationClass) {
        final Class<? extends Interceptor> cls = annotationRegistration.get(annotationClass);
        if (cls != null) {
            return cls;
        }
        Class<? extends MethodAnnotationLifeCycle> interceptorClass = null;
        final MethodLifeCycle methodLifeCycleAnn = annotationClass.getAnnotation(MethodLifeCycle.class);
        if (methodLifeCycleAnn == null) {
            final BaseLifeCycle baseLifeCycle = annotationClass.getAnnotation(BaseLifeCycle.class);
            if (baseLifeCycle != null) {
                final Class<? extends BaseAnnotationLifeCycle> baseAnnLifeCycleClass = baseLifeCycle.value();
                if (baseAnnLifeCycleClass != null) {
                    if (MethodAnnotationLifeCycle.class.isAssignableFrom(baseAnnLifeCycleClass)) {
                        interceptorClass = (Class<? extends MethodAnnotationLifeCycle>) baseAnnLifeCycleClass;
                    } else {
                        registerAnnotation(annotationClass, baseAnnLifeCycleClass);
                        return baseAnnLifeCycleClass;
                    }
                }
            }

            final ParamLifeCycle paramLifeCycleAnn = annotationClass.getAnnotation(ParamLifeCycle.class);
            if (paramLifeCycleAnn != null) {
                final Class<? extends ParameterAnnotationLifeCycle> paramInterceptorClass = paramLifeCycleAnn.value();
                if (!Interceptor.class.isAssignableFrom(paramInterceptorClass)) {
                    throw new ForestInterceptorDefineException(paramInterceptorClass);
                }
                if (paramInterceptorClass != null) {
                    registerAnnotation(annotationClass, paramInterceptorClass);
                }
                return paramInterceptorClass;
            }

        }
        if (methodLifeCycleAnn != null || interceptorClass != null) {
            if (interceptorClass == null) {
                interceptorClass = methodLifeCycleAnn.value();
                if (!Interceptor.class.isAssignableFrom(interceptorClass)) {
                    throw new ForestInterceptorDefineException(interceptorClass);
                }
            }
        }
        if (interceptorClass != null) {
            registerAnnotation(annotationClass, interceptorClass);
        }
        return interceptorClass;

    }

    /**
     * 创建请求接口的动态代理实例
     *
     * @param clazz 请求接口类
     * @param <T>   请求接口类泛型
     * @return 动态代理实例
     */
    public <T> T client(final Class<T> clazz) {
        return createInstance(clazz);
    }

    public <T> T clientFromCache(final Class<T> clazz) {
        return (T) CLIENT_CACHE.computeIfAbsent(clazz, key -> createInstance(key));
    }



    /**
     * 设置全局JSON数据转换器的选择器
     *
     * @param jsonConverterSelector JSON转换器选择策略对象，{@link JSONConverterSelector}类实例
     * @return 当前ForestConfiguration实例
     */
    private ForestConfiguration setJsonConverterSelector(final JSONConverterSelector jsonConverterSelector) {
        this.jsonConverterSelector = jsonConverterSelector;
        return this;
    }


    /**
     * 注册全局过滤器
     *
     * @param name        过滤器名称
     * @param filterClass 过滤器类
     * @return 当前ForestConfiguration实例
     */
    public ForestConfiguration registerFilter(final String name, final Class filterClass) {
        if (!(Filter.class.isAssignableFrom(filterClass))) {
            throw new ForestRuntimeException("Cannot register class \"" + filterClass.getName()
                    + "\" as a filter, filter class must implement Filter interface!");
        }
        filterRegisterMap.put(name, filterClass);
        return this;
    }

    /**
     * 获取全局过滤器注册表中所有过滤器的名称列表
     *
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
     *
     * @param name 过滤器名称
     * @return 如果存在为 {@code true}, 否则为 {@code false}
     */
    public boolean hasFilter(final String name) {
        return filterRegisterMap.containsKey(name);
    }

    /**
     * 根据过滤器名称创建新的过滤器实例
     *
     * @param name 过滤器名称
     * @return 新的Filter实例
     */
    public Filter newFilterInstance(final String name) {
        final Class<?> filterClass = filterRegisterMap.get(name);
        if (filterClass == null) {
            throw new ForestRuntimeException("filter \"" + name + "\" does not exists!");
        }
        try {
            return (Filter) filterClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ForestRuntimeException("An error occurred the initialization of filter \"" + name + "\" ! cause: " + e.getMessage(), e);
        }
    }


    public <R> ForestRequest<R> makeRequest(final Class<R> clazz) {
        final MethodLifeCycleHandler<R> lifeCycleHandler = new MethodLifeCycleHandler<>(clazz, clazz);
        final ForestRequest<R> request = new ForestRequest<>(this, new ForestRequestContext(variableContext, null))
                .backend(getBackend())
                .setLogConfiguration(new LogConfiguration()
                        .setLogHandler(getLogHandler())
                        .setLogEnabled(isLogEnabled())
                        .setLogRequest(isLogRequest())
                        .setLogResponseStatus(isLogResponseStatus())
                        .setLogResponseContent(isLogResponseContent()))
                .asyncMode(getAsyncMode())
                .setLifeCycleHandler(lifeCycleHandler)
                .setRetryer(getRetryer());
        if (CollectionUtils.isNotEmpty(interceptors)) {
            for (Class<? extends Interceptor> interceptorClass : interceptors) {
                request.addInterceptor(interceptorClass);
            }
        }
        return request;
    }

    public ForestRequest<?> makeRequest() {
        return makeRequest(Object.class);
    }

    /**
     * 创建通用 {@link ForestRequest} 对象
     *
     * @return {@link ForestRequest} 对象
     */
    public ForestRequest<?> request() {
        return makeRequest()
                .setType(ForestRequestType.GET)
                .setUrl("/");
    }

    /**
     * 创建通用 {@link ForestRequest} 对象
     *
     * @param clazz 返回结果类型
     * @param <R>   返回结果类型泛型参数
     * @return {@link ForestRequest} 对象
     */
    public <R> ForestRequest<R> request(final Class<R> clazz) {
        return makeRequest(clazz)
                .setType(ForestRequestType.GET)
                .setUrl("/");
    }

    /**
     * 创建 GET 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     */
    public ForestRequest<?> get(final String url) {
        return makeRequest()
                .setType(ForestRequestType.GET)
                .setUrl(url);
    }

    /**
     * 创建 POST 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     */
    public ForestRequest<?> post(final String url) {
        return makeRequest()
                .setType(ForestRequestType.POST)
                .setUrl(url);
    }

    /**
     * 创建 PUT 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     */
    public ForestRequest<?> put(final String url) {
        return makeRequest()
                .setType(ForestRequestType.PUT)
                .setUrl(url);

    }

    /**
     * 创建 DELETE 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     */
    public ForestRequest<?> delete(final String url) {
        return makeRequest()
                .setType(ForestRequestType.DELETE)
                .setUrl(url);

    }

    /**
     * 创建 HEAD 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     */
    public ForestRequest<?> head(final String url) {
        return makeRequest()
                .setType(ForestRequestType.HEAD)
                .setUrl(url);

    }

    /**
     * 创建 PATCH 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     */
    public ForestRequest<?> patch(final String url) {
        return makeRequest()
                .setType(ForestRequestType.PATCH)
                .setUrl(url);
    }

    /**
     * 创建 OPTIONS 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     */
    public ForestRequest<?> options(final String url) {
        return makeRequest()
                .setType(ForestRequestType.OPTIONS)
                .setUrl(url);

    }

    /**
     * 创建 TRACE 请求的 {@link ForestRequest} 对象
     *
     * @param url 请求 URL
     * @return {@link ForestRequest} 对象
     */
    public ForestRequest<?> trace(final String url) {
        return makeRequest()
                .setType(ForestRequestType.TRACE)
                .setUrl(url);
    }


}
