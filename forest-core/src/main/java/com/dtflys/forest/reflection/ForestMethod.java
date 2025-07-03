package com.dtflys.forest.reflection;

import com.dtflys.forest.annotation.BaseLifeCycle;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.MethodLifeCycle;
import com.dtflys.forest.annotation.ParamLifeCycle;
import com.dtflys.forest.annotation.RequestAttributes;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.callback.AddressSource;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnLoadCookie;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.callback.OnRedirection;
import com.dtflys.forest.callback.OnSaveCookie;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.exceptions.ForestInterceptorDefineException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.filter.Filter;
import com.dtflys.forest.http.ForestAddress;
import com.dtflys.forest.http.ForestQueryMap;
import com.dtflys.forest.http.ForestQueryParameter;
import com.dtflys.forest.http.ForestSSE;
import com.dtflys.forest.http.SimpleQueryParameter;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.ForestRequestType;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestURL;
import com.dtflys.forest.http.body.RequestBodyBuilder;
import com.dtflys.forest.http.body.StringRequestBody;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.interceptor.InterceptorAttributes;
import com.dtflys.forest.interceptor.InterceptorFactory;
import com.dtflys.forest.lifecycles.BaseAnnotationLifeCycle;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.lifecycles.method.RequestLifeCycle;
import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.logging.ForestLogHandler;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.mapping.MappingURLTemplate;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.mapping.SubVariableScope;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.multipart.ForestMultipartFactory;
import com.dtflys.forest.proxy.InterfaceProxyHandler;
import com.dtflys.forest.retryer.ForestRetryer;
import com.dtflys.forest.sse.ForestSSEListener;
import com.dtflys.forest.ssl.SSLKeyStore;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.HeaderUtils;
import com.dtflys.forest.utils.NameUtils;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.dtflys.forest.mapping.MappingParameter.*;

/**
 * 通过代理调用的实际执行的方法对象
 *
 * @author gongjun
 * @since 2016-05-03
 */
public class ForestMethod<T> implements VariableScope {
    // 默认根地址
    private static final ForestAddress DEFAULT_ADDRESS = new ForestAddress("http", "localhost", -1);
    private final InterfaceProxyHandler interfaceProxyHandler;
    private final ForestConfiguration configuration;
    private volatile boolean initialized = false;
    private final Object INIT_LOCK = new Object();
    private InterceptorFactory interceptorFactory;
    private final Method method;
    private String[] methodNameItems;
    private Class returnClass;
    private Type returnType;
    private MappingParameter returnTypeParameter;
    private MetaRequest metaRequest;
    private MappingURLTemplate baseUrlTemplate;
    private MappingURLTemplate urlTemplate;
    private MappingTemplate typeTemplate;
    private MappingTemplate dataTypeTemplate;
    private MappingTemplate bodyTypeTemplate;
    private Integer baseTimeout = null;
    private Integer baseConnectTimeout = null;
    private Integer baseReadTimeout = null;
    private Integer timeout = null;
    private Integer connectTimeout = null;
    private Integer readTimeout = null;
    private MappingTemplate sslProtocolTemplate;
    private Class baseRetryerClass = null;
    private Integer baseRetryCount = null;
    private Long baseMaxRetryInterval;
    private Integer retryCount = null;
    private long maxRetryInterval;
    private MappingTemplate baseEncodeTemplate = null;
    private MappingTemplate encodeTemplate = null;
    private MappingTemplate charsetTemplate = null;
    private MappingTemplate responseEncodingTemplate = null;
    private MappingTemplate baseContentTypeTemplate;
    private MappingTemplate baseUserAgentTemplate;
    private MappingTemplate baseCharsetTemplate;
    private MappingTemplate baseSslProtocolTemplate;
    private MappingTemplate contentTypeTemplate;
    private MappingTemplate userAgentTemplate;
    private long progressStep = -1;
    private ForestEncoder encoder = null;
    private ForestConverter decoder = null;
    private MappingTemplate sslKeyStoreId;
    private MappingTemplate[] dataTemplateArray;
    private MappingTemplate[] headerTemplateArray;
    private MappingParameter[] parameterTemplateArray;
    private MappingParameter[] forestParameters;
    private final List<MappingParameter> namedParameters = new ArrayList<>();
    private final List<ForestMultipartFactory> multipartFactories = new ArrayList<>();
    private final Map<String, ForestVariable> variables = new ConcurrentHashMap<>();
    private final Map<String, MappingTemplate> templateCache = new ConcurrentHashMap<>();
    private MappingParameter onSuccessParameter = null;
    private MappingParameter onErrorParameter = null;
    private MappingParameter onRedirectionParameter = null;
    private MappingParameter onProgressParameter = null;
    private MappingParameter onLoadCookieParameter = null;
    private MappingParameter onSaveCookieParameter = null;
    private List<Interceptor> globalInterceptorList;
    private List<Interceptor> baseInterceptorList;
    private List<Interceptor> interceptorList;
    private List<InterceptorAttributes> interceptorAttributesList;
    private Type onSuccessClassGenericType = null;
    private Class retryerClass = null;
    private boolean async = false;

    private LogConfiguration baseLogConfiguration = null;

    private boolean logEnabled = true;
    private boolean logRequest = true;
    private boolean logRequestHeaders = true;
    private boolean logRequestBody = true;
    private boolean logResponseStatus = true;
    private boolean logResponseHeaders = true;
    private boolean logResponseContent = false;
    private ForestLogHandler logHandler = null;
    private LogConfiguration logConfiguration = null;
    private Map<String, Object> extensionParameters = new ConcurrentHashMap<>();

    public ForestMethod(InterfaceProxyHandler interfaceProxyHandler, ForestConfiguration configuration, Method method) {
        this.interfaceProxyHandler = interfaceProxyHandler;
        this.configuration = configuration;
        this.method = method;
        this.interceptorFactory = configuration.getInterceptorFactory();
        this.methodNameItems = NameUtils.splitCamelName(method.getName());
        this.forestParameters = new MappingParameter[method.getParameterCount()];
    }

    public void initMethod() {
        if (!initialized) {
            synchronized (INIT_LOCK) {
                if (!initialized) {
                    processBaseProperties();
                    processMethodAnnotations();
                    initialized = true;
                }
            }
        }
    }

    @Override
    public ForestConfiguration getConfiguration() {
        return configuration;
    }



    @Override
    public boolean isVariableDefined(String name) {
        return configuration.isVariableDefined(name);
    }

    @Override
    public Object getVariableValue(String name) {
        return getVariableValue(name, this);
    }

    @Deprecated
    public Object getVariableValue(String name, ForestMethod method) {
        ForestVariable variable = getVariable(name);
        if (variable == null) {
            return null;
        }
        if (variable instanceof ForestMethodVariable) {
            return ((ForestMethodVariable) variable).getValue(method);
        }
        return variable.getValue(null);
    }


    @Override
    public <R> R getVariableValue(String name, Class<R> clazz) {
        return getVariableValue(name, null, clazz);
    }

    @Override
    public Object getVariableValue(String name, ForestRequest request) {
        ForestVariable variable = getVariable(name);
        if (variable == null) {
            return null;
        }
        return variable.getValue(request);
    }

    @Override
    public <R> R getVariableValue(String name, ForestRequest request, Class<R> clazz) {
        ForestVariable variable = getVariable(name);
        if (variable == null) {
            return null;
        }
        return variable.getValue(request, clazz);
    }

    public MappingParameter[] getForestParameters() {
        return forestParameters;
    }

    public MappingTemplate makeTemplate(MappingParameter parameter) {
        return getOrCreateTemplate(null, null, parameter.getName());
//        return new MappingTemplate(null, null, this, parameter.getName(), this, configuration.getProperties(), forestParameters);
    }


    public MappingTemplate makeTemplate(
            final Class<? extends Annotation> annotationType,
            final String attributeName,
            final String text) {
        return getOrCreateTemplate(annotationType, attributeName, text);
//        return new MappingTemplate(annotationType, attributeName, this, text, this, configuration.getProperties(), forestParameters);
    }
    
    public MappingTemplate makeTemplate(final String text) {
        return makeTemplate(null, null, text);
    }

    public MappingURLTemplate makeURLTemplate(
            final Class<? extends Annotation> annotationType,
            final String attributeName,
            final String text) {
        return new MappingURLTemplate(
                MappingTemplate.METHOD_URL,
                annotationType,
                attributeName,
                this,
                text,
                configuration.getProperties(),
                forestParameters);
    }

    private MappingTemplate getOrCreateTemplate(
            final Class<? extends Annotation> annotationType,
            final String attributeName,
            final String text) {
        final String key = (annotationType != null ? annotationType.getName() : "") + "@" + (attributeName != null ? attributeName : "") + "@" + text;
        final MappingTemplate template = templateCache.get(key);
        if (template == null) {
            synchronized (templateCache) {
                if (template == null) {
                    final MappingTemplate newTemplate =
                            new MappingTemplate(
                                    MappingTemplate.METHOD_TEMPLATE,
                                    annotationType,
                                    attributeName,
                                    this,
                                    text,
                                    forestParameters);
                    if (templateCache.size() < 128) {
                        templateCache.put(key, newTemplate);
                    }
                    return newTemplate;
                }
            }
        }
        return template;
    }


    /**
     * 获取Forest接口方法的返回类
     *
     * @return 返回类 {@link Class} 实例
     */
    public Class getReturnClass() {
        return returnClass;
    }


    @Override
    public ForestVariable getVariable(String name) {
        ForestVariable variable = variables.get(name);
        if (variable == null && configuration != null) {
            return configuration.getVariable(name);
        }
        return variable;
    }

    public MappingParameter[] getParameters() {
        return forestParameters;
    }

    private void processBaseProperties() {
        MetaRequest baseMetaRequest = interfaceProxyHandler.getBaseMetaRequest();
        String baseUrl = baseMetaRequest.getUrl();
        if (StringUtils.isNotBlank(baseUrl)) {
            baseUrlTemplate = makeURLTemplate(BaseRequest.class, "baseUrl", baseUrl);
        }
        String baseContentEncoding = baseMetaRequest.getContentEncoding();
        if (StringUtils.isNotBlank(baseContentEncoding)) {
            baseEncodeTemplate = makeTemplate(BaseRequest.class, "contentEncoding", baseContentEncoding);
        }
        String baseContentType = baseMetaRequest.getContentType();
        if (StringUtils.isNotBlank(baseContentType)) {
            baseContentTypeTemplate = makeTemplate(BaseRequest.class, "contentType", baseContentType);
        }
        String baseUserAgent = baseMetaRequest.getUserAgent();
        if (StringUtils.isNotBlank(baseUserAgent)) {
            baseUserAgentTemplate = makeTemplate(BaseRequest.class, "userAgent", baseUserAgent);
        }
        String baseCharset = baseMetaRequest.getCharset();
        if (StringUtils.isNotBlank(baseCharset)) {
            baseCharsetTemplate = makeTemplate(BaseRequest.class, "charset", baseCharset);
        }
        String baseSslProtocol = baseMetaRequest.getSslProtocol();
        if (StringUtils.isNotBlank(baseSslProtocol)) {
            baseSslProtocolTemplate = makeTemplate(BaseRequest.class, "sslProtocol", baseSslProtocol);
        }

        baseLogConfiguration = interfaceProxyHandler.getBaseLogConfiguration();

        baseTimeout = baseMetaRequest.getTimeout();
        baseConnectTimeout = baseMetaRequest.getConnectTimeout();
        baseReadTimeout = baseMetaRequest.getReadTimeout();
        baseRetryerClass = baseMetaRequest.getRetryer();
        baseRetryCount = baseMetaRequest.getRetryCount();
        baseMaxRetryInterval = baseMetaRequest.getMaxRetryInterval();

        List<Class<? extends Interceptor>> globalInterceptorClasses = configuration.getInterceptors();
        if (globalInterceptorClasses != null && globalInterceptorClasses.size() > 0) {
            globalInterceptorList = new LinkedList<>();
            for (Class clazz : globalInterceptorClasses) {
                if (!Interceptor.class.isAssignableFrom(clazz) || clazz.isInterface()) {
                    throw new ForestRuntimeException("Class [" + clazz.getName() + "] is not a implement of [" +
                            Interceptor.class.getName() + "] interface.");
                }
                Interceptor interceptor = interceptorFactory.getInterceptor(clazz);
                globalInterceptorList.add(interceptor);
            }
        }

        Class[] baseInterceptorClasses = baseMetaRequest.getInterceptor();
        if (baseInterceptorClasses != null && baseInterceptorClasses.length > 0) {
            baseInterceptorList = new LinkedList<>();
            for (int cidx = 0, len = baseInterceptorClasses.length; cidx < len; cidx++) {
                Class clazz = baseInterceptorClasses[cidx];
                if (!Interceptor.class.isAssignableFrom(clazz) || clazz.isInterface()) {
                    throw new ForestRuntimeException("Class [" + clazz.getName() + "] is not a implement of [" +
                            Interceptor.class.getName() + "] interface.");
                }
                Interceptor interceptor = interceptorFactory.getInterceptor(clazz);
                baseInterceptorList.add(interceptor);
            }
        }
    }

    private <T extends Interceptor> T addInterceptor(Class<T> interceptorClass) {
        if (interceptorList == null) {
            interceptorList = new LinkedList<>();
        }
        if (!Interceptor.class.isAssignableFrom(interceptorClass) || interceptorClass.isInterface()) {
            throw new ForestRuntimeException("Class [" + interceptorClass.getName() + "] is not a implement of [" +
                    Interceptor.class.getName() + "] interface.");
        }
        T interceptor = interceptorFactory.getInterceptor(interceptorClass);
        interceptorList.add(interceptor);
        return interceptor;
    }


    private Map<Annotation, ForestAnnotation> getForestAnnotationMap(Annotation annotation) {
        Class<? extends Annotation> annType = annotation.annotationType();
        String annName = annType.getPackage().getName();
        if (annName.startsWith("java.")
                || annName.startsWith("javax.")
                || annName.startsWith("kotlin")) {
            return null;
        }
        Map<Annotation, ForestAnnotation> resultMap = new LinkedHashMap<>();
        Annotation[] annArray = annType.getAnnotations();
        for (Annotation parentAnn : annArray) {
            Map<Annotation, ForestAnnotation> parentMap = getForestAnnotationMap(parentAnn);
            if (parentMap != null && !parentMap.isEmpty()) {
                for (ForestAnnotation parentForestAnn : parentMap.values()) {
                    parentForestAnn.annotations.add(annotation);
                }
                resultMap.putAll(parentMap);
            }
        }
        Class<? extends Interceptor> lifeCycleClass = getAnnotationLifeCycleClass(annotation);
        if (lifeCycleClass != null) {
            ForestAnnotation forestAnnotation = new ForestAnnotation(this, annotation, lifeCycleClass);
            resultMap.put(annotation, forestAnnotation);
        }
        return resultMap;
    }

    private Class<? extends Interceptor> getAnnotationLifeCycleClass(Annotation annotation) {
        Class<? extends Annotation> annType = annotation.annotationType();
        Class<? extends MethodAnnotationLifeCycle> interceptorClass = null;
        MethodLifeCycle methodLifeCycleAnn = annType.getAnnotation(MethodLifeCycle.class);
        if (methodLifeCycleAnn == null) {
            BaseLifeCycle baseLifeCycle = annType.getAnnotation(BaseLifeCycle.class);
            if (baseLifeCycle != null) {
                Class<? extends BaseAnnotationLifeCycle> baseAnnLifeCycleClass = baseLifeCycle.value();
                if (baseAnnLifeCycleClass != null) {
                    if (MethodAnnotationLifeCycle.class.isAssignableFrom(baseAnnLifeCycleClass)) {
                        interceptorClass = (Class<? extends MethodAnnotationLifeCycle>) baseAnnLifeCycleClass;
                    } else {
                        return baseAnnLifeCycleClass;
                    }
                }
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

        return interceptorClass;
    }

    /**
     * 设置扩展参数值
     *
     * @param name  参数名
     * @param value 参数值
     */
    public void setExtensionParameterValue(String name, Object value) {
        this.extensionParameters.put(name, value);
    }

    /**
     * 获取扩展参数值
     *
     * @param name 参数名
     * @return 参数值
     */
    public Object getExtensionParameterValue(String name) {
        return this.extensionParameters.get(name);
    }

    /**
     * 添加元请求注释表
     *
     * @param anns 请求注释表
     */
    private void addMetaRequestAnnotations(List<ForestAnnotation> anns) {
        for (ForestAnnotation ann : anns) {
            addMetaRequestAnnotation(ann);
        }
    }

    /**
     * 添加元请求注释
     */
    private void addMetaRequestAnnotation(final ForestAnnotation forestAnnotation) {
        final Class<? extends Interceptor> interceptorClass = forestAnnotation.interceptor;
        Map<String, Object> attrTemplates = forestAnnotation.getAttributeTemplates();
        if (attrTemplates != null) {
            InterceptorAttributes attributes = new InterceptorAttributes(interceptorClass, attrTemplates);
            if (interceptorAttributesList == null) {
                interceptorAttributesList = new LinkedList<>();
            }
            interceptorAttributesList.add(attributes);
        }
        Interceptor interceptor = addInterceptor(interceptorClass);
        if (interceptor instanceof MethodAnnotationLifeCycle) {
            MethodAnnotationLifeCycle lifeCycle = (MethodAnnotationLifeCycle) interceptor;
            lifeCycle.onMethodInitialized(this, forestAnnotation.annotation);
        }
    }


    public void setMetaRequest(MetaRequest metaRequest) {
        if (metaRequest != null && this.metaRequest != null) {
            throw new ForestRuntimeException("[Forest] annotation \""
                    + metaRequest.getRequestAnnotation().annotationType().getName() + "\" can not be added on method \""
                    + method.getName() + "\", because a similar annotation \""
                    + metaRequest.getRequestAnnotation().annotationType().getName() + "\" has already been attached to this method.");
        }
        this.metaRequest = metaRequest;
    }

    /**
     * 获取Forest方法对应的Java原生方法
     *
     * @return Java原生方法，{@link java.lang.reflect.Method}类实例
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 获取方法名
     *
     * @return 方法名字符串
     */
    public String getMethodName() {
        return method.getName();
    }

    /**
     * 获取元请求信息
     *
     * @return 元请求对象，{@link MetaRequest}类实例
     */
    public MetaRequest getMetaRequest() {
        return metaRequest;
    }

    private static class ForestAnnotation {

        private final ForestMethod<?> method;

        private final List<Annotation> annotations = new LinkedList<>();

        private final Annotation annotation;
        private final Class<? extends Interceptor> interceptor;

        private ForestAnnotation(ForestMethod<?> method, Annotation annotation, Class<? extends Interceptor> interceptor) {
            this.method = method;
            this.annotation = annotation;
            this.interceptor = interceptor;
            this.annotations.add(annotation);
        }

        public Annotation getAnnotation() {
            return annotation;
        }

        public Class<? extends Interceptor> getInterceptor() {
            return interceptor;
        }

        public Map<String, Object> getAttributeTemplates() {
            Map<String, Object> results = null;
            Annotation root = null;
            for (final Annotation ann : annotations) {
                final Class<? extends Annotation> annType = ann.annotationType();
                final RequestAttributes requestAttributesAnn = annType.getAnnotation(RequestAttributes.class);
                if (requestAttributesAnn != null) {
                    final Map<String, Object> attrTemplates = ReflectUtils.getAttributesFromAnnotation(ann, root != null);
                    for (final String key : attrTemplates.keySet()) {
                        final Object value = attrTemplates.get(key);
                        if (value instanceof CharSequence) {
                            final MappingTemplate template = method.makeTemplate(annType, key, value.toString());
                            attrTemplates.put(key, template);
                        } else if (String[].class.isAssignableFrom(value.getClass())) {
                            final String[] stringArray = (String[]) value;
                            final int len = stringArray.length;
                            final MappingTemplate[] templates = new MappingTemplate[stringArray.length];
                            for (int i = 0; i < len; i++) {
                                final String item = stringArray[i];
                                final MappingTemplate template = method.makeTemplate(annType, key, item);
                                templates[i] = template;
                            }
                            attrTemplates.put(key, templates);
                        }
                    }
                    if (!attrTemplates.isEmpty()) {
                        if (results == null) {
                            results = new ConcurrentHashMap<>();
                            root = ann;
                        }
                        results.putAll(attrTemplates);
                    }
                }
            }
            return results;
        }
    }

    private void fetchAnnotationsFromClasses(List<Annotation> annotationList, Class[] classes) {
        for (Class clazz : classes) {
            if (clazz == null || clazz == Object.class) {
                continue;
            }
            fetchAnnotationsFromClasses(annotationList, clazz.getInterfaces());
            for (Annotation ann : clazz.getAnnotations()) {
                annotationList.add(ann);
            }
        }
    }

    /**
     * 处理方法上的注解列表
     */
    private void processMethodAnnotations() {
        List<Annotation> annotationList = new LinkedList<>();
        fetchAnnotationsFromClasses(annotationList, new Class[]{interfaceProxyHandler.getInterfaceClass()});

        for (Annotation ann : method.getAnnotations()) {
            annotationList.add(ann);
        }

        List<ForestAnnotation> requestAnns = new LinkedList<>();
        List<ForestAnnotation> methodAnns = new LinkedList<>();

        // 对注解分类
        for (Annotation ann : annotationList) {
            if (ann instanceof BaseRequest) {
                continue;
            }
            final Map<Annotation, ForestAnnotation> forestAnnotationMap = getForestAnnotationMap(ann);
            if (forestAnnotationMap != null && !forestAnnotationMap.isEmpty()) {
                for (final Map.Entry<Annotation, ForestAnnotation> entry : forestAnnotationMap.entrySet()) {
                    final ForestAnnotation forestAnnotation = entry.getValue();
                    final Class<? extends Interceptor> lifeCycleClass = forestAnnotation.interceptor;
                    if (RequestLifeCycle.class.isAssignableFrom(lifeCycleClass)) {
                        requestAnns.add(forestAnnotation);
                    } else {
                        methodAnns.add(forestAnnotation);
                    }
                }
            }
        }

        // 先添加请求类注解
        addMetaRequestAnnotations(requestAnns);

        // 再添加普通方法类注解
        addMetaRequestAnnotations(methodAnns);

        // 处理请求元信息
        if (this.metaRequest != null) {
            processMetaRequest(this.metaRequest);
        }

        returnClass = method.getReturnType();
    }

    private void processMetaRequest(MetaRequest metaRequest) {
        final Class<?>[] paramTypes = method.getParameterTypes();
        final Type[] genericParamTypes = method.getGenericParameterTypes();
        final Annotation[][] paramAnns = method.getParameterAnnotations();
        final Parameter[] parameters = method.getParameters();
        final Class<? extends Annotation> reqAnnType = metaRequest.getRequestAnnotation().annotationType();
        parameterTemplateArray = new MappingParameter[paramTypes.length];
        processParameters(parameters, genericParamTypes, paramAnns);
        bodyTypeTemplate = makeTemplate(reqAnnType, "type", metaRequest.getBodyType());
        urlTemplate = makeURLTemplate(reqAnnType, "url", metaRequest.getUrl());
        typeTemplate = makeTemplate(reqAnnType, "type", metaRequest.getType());
        dataTypeTemplate = makeTemplate(reqAnnType, "dataType", metaRequest.getDataType());
        if (StringUtils.isNotEmpty(metaRequest.getContentType())) {
            contentTypeTemplate = makeTemplate(reqAnnType, "contentType", metaRequest.getContentType());
        }
        if (StringUtils.isNotEmpty(metaRequest.getUserAgent())) {
            userAgentTemplate = makeTemplate(reqAnnType, "userAgent", metaRequest.getUserAgent());
        }
        sslKeyStoreId = makeTemplate(reqAnnType, "keyStore", metaRequest.getKeyStore());
        if (StringUtils.isNotEmpty(metaRequest.getContentEncoding())) {
            encodeTemplate = makeTemplate(reqAnnType, "contentEncoding", metaRequest.getContentEncoding());
        }
        charsetTemplate = makeTemplate(reqAnnType, "charset", metaRequest.getCharset());
        if (metaRequest.getResponseEncoding() != null) {
            responseEncodingTemplate = makeTemplate(reqAnnType, "responseEncoding", metaRequest.getResponseEncoding());
        }
        sslProtocolTemplate = makeTemplate(reqAnnType, "sslProtocol", metaRequest.getSslProtocol());
        progressStep = metaRequest.getProgressStep();
        async = metaRequest.isAsync();
        retryerClass = metaRequest.getRetryer();
        final Class<? extends ForestEncoder> encoderClass = metaRequest.getEncoder();
        final Class<? extends ForestConverter> decoderClass = metaRequest.getDecoder();
        final String[] dataArray = metaRequest.getData();
        final String[] headerArray = metaRequest.getHeaders();
        final Integer tout = metaRequest.getTimeout();
        if (tout != null && tout >= 0) {
            timeout = tout;
        }
        final Integer ctout = metaRequest.getConnectTimeout();
        if (ctout != null && ctout >= 0) {
            connectTimeout = ctout;
        }
        final Integer rtout = metaRequest.getReadTimeout();
        if (rtout != null && rtout >= 0) {
            readTimeout = rtout;
        }
        final Integer rtnum = metaRequest.getRetryCount();
        if (rtnum != null && rtnum >= 0) {
            retryCount = rtnum;
        }
        maxRetryInterval = metaRequest.getMaxRetryInterval();
        logEnabled = configuration.isLogEnabled();
        if (!logEnabled) {
            logEnabled = metaRequest.isLogEnabled();
        }
        logRequest = configuration.isLogRequest();
        logRequestHeaders = configuration.isLogRequestHeaders();
        logRequestBody = configuration.isLogRequestBody();
        logResponseStatus = configuration.isLogResponseStatus();
        logResponseHeaders = configuration.isLogResponseHeaders();
        logResponseContent = configuration.isLogResponseContent();

        LogConfiguration metaLogConfiguration = metaRequest.getLogConfiguration();
        if (metaLogConfiguration == null && baseLogConfiguration != null) {
            metaLogConfiguration = baseLogConfiguration;
        }
        if (metaLogConfiguration != null) {
            logEnabled = metaLogConfiguration.isLogEnabled();
            logRequest = metaLogConfiguration.isLogRequest();
            logRequestHeaders = metaLogConfiguration.isLogRequestHeaders();
            logRequestBody = metaLogConfiguration.isLogRequestBody();
            logResponseStatus = metaLogConfiguration.isLogResponseStatus();
            logResponseHeaders = metaLogConfiguration.isLogResponseHeaders();
            logResponseContent = metaLogConfiguration.isLogResponseContent();
            logHandler = metaLogConfiguration.getLogHandler();
            if (logHandler == null && baseLogConfiguration != null) {
                logHandler = baseLogConfiguration.getLogHandler();
            }
        }
        if (logHandler == null && configuration.getLogHandler() != null) {
            logHandler = configuration.getLogHandler();
        }
        if (logHandler == null) {
            logHandler = new DefaultLogHandler();
        }

        logConfiguration = new LogConfiguration();
        logConfiguration.setLogEnabled(logEnabled);
        logConfiguration.setLogRequest(logRequest);
        logConfiguration.setLogRequestHeaders(logRequestHeaders);
        logConfiguration.setLogRequestBody(logRequestBody);
        logConfiguration.setLogResponseStatus(logResponseStatus);
        logConfiguration.setLogResponseHeaders(logResponseHeaders);
        logConfiguration.setLogResponseContent(logResponseContent);
        logConfiguration.setLogHandler(logHandler);


        dataTemplateArray = new MappingTemplate[dataArray.length];
        for (int j = 0; j < dataArray.length; j++) {
            String data = dataArray[j];
            MappingTemplate dataTemplate = makeTemplate(reqAnnType, "data", data);
            dataTemplateArray[j] = dataTemplate;
        }

        headerTemplateArray = new MappingTemplate[headerArray.length];
        for (int j = 0; j < headerArray.length; j++) {
            String header = headerArray[j];
            MappingTemplate headerTemplate = makeTemplate(reqAnnType, "header", header);
            headerTemplateArray[j] = headerTemplate;
        }

        final Class<? extends Interceptor>[] interceptorClasses = metaRequest.getInterceptor();
        if (interceptorClasses != null && interceptorClasses.length > 0) {
            for (int cidx = 0, len = interceptorClasses.length; cidx < len; cidx++) {
                final Class<? extends Interceptor> interceptorClass = interceptorClasses[cidx];
                addInterceptor(interceptorClass);
            }
        }

        if (encoderClass != null && !encoderClass.isInterface()
                && ForestEncoder.class.isAssignableFrom(encoderClass)) {
            this.encoder = configuration.getForestObjectFactory().getObject(encoderClass);
        }

        if (decoderClass != null && !decoderClass.isInterface()
                && ForestConverter.class.isAssignableFrom(decoderClass)) {
            this.decoder = configuration.getForestObjectFactory().getObject(decoderClass);
        }
    }

    /**
     * 处理参数列表
     *
     * @param parameters        参数数组，{@link Parameter}类数组实例
     * @param genericParamTypes 参数类型数组
     * @param paramAnns         参数注解的二维数组
     */
    private void processParameters(Parameter[] parameters, Type[] genericParamTypes, Annotation[][] paramAnns) {
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            Class paramType = param.getType();
            Annotation[] anns = paramAnns[i];
            MappingParameter parameter = new MappingParameter(paramType);
            parameter.setIndex(i);
            parameter.setName(param.getName());
            parameterTemplateArray[i] = parameter;
            if (OnSuccess.class.isAssignableFrom(paramType)) {
                onSuccessParameter = parameter;
                Type genType = genericParamTypes[i];
                onSuccessClassGenericType = getGenericClassOrType(genType, 0);
            } else if (OnError.class.isAssignableFrom(paramType)) {
                onErrorParameter = parameter;
            } else if (OnRedirection.class.isAssignableFrom(paramType)) {
                onRedirectionParameter = parameter;
            } else if (OnProgress.class.isAssignableFrom(paramType)) {
                onProgressParameter = parameter;
            } else if (OnSaveCookie.class.isAssignableFrom(paramType)) {
                onSaveCookieParameter = parameter;
            } else if (OnLoadCookie.class.isAssignableFrom(paramType)) {
                onLoadCookieParameter = parameter;
            }
            processParameterAnnotation(parameter, anns);
        }
    }

    /**
     * 处理参数的注解
     *
     * @param parameter 方法参数-字符串模板解析对象，{@link MappingParameter}类实例
     * @param anns      方法参数注解的二维数组
     */
    private void processParameterAnnotation(MappingParameter parameter, Annotation[] anns) {
        for (int i = 0; i < anns.length; i++) {
            Annotation ann = anns[i];
            Class annType = ann.annotationType();
            if (annType.getPackage().getName().startsWith("java.")) {
                continue;
            }
            Annotation[] subAnnArray = annType.getAnnotations();
            if (subAnnArray.length > 0) {
                processParameterAnnotation(parameter, subAnnArray);
            }
            ParamLifeCycle paramLifeCycleAnn = (ParamLifeCycle) annType.getAnnotation(ParamLifeCycle.class);
            if (paramLifeCycleAnn != null) {
                Class<? extends ParameterAnnotationLifeCycle> interceptorClass = paramLifeCycleAnn.value();
                if (!Interceptor.class.isAssignableFrom(interceptorClass)) {
                    throw new ForestInterceptorDefineException(interceptorClass);
                }
                ParameterAnnotationLifeCycle lifeCycle = addInterceptor(interceptorClass);
                lifeCycle.onParameterInitialized(this, parameter, ann);
            }
        }
    }

    /**
     * 添加命名参数
     *
     * @param parameter 方法参数-字符串模板解析对象，{@link MappingParameter}类实例
     */
    public void addNamedParameter(MappingParameter parameter) {
        Integer index = parameter.getIndex();
        if (index != null && forestParameters[index] != null) {
            return;
        }
        namedParameters.add(parameter);
        if (index != null) {
            forestParameters[index] = parameter;
        }
    }

    /**
     * 添加变量
     *
     * @param name     变量名
     * @param variable 变量对象，{@link MappingVariable}类实例
     */
    public void addVariable(String name, ForestVariable variable) {
        variables.put(name, variable);
    }

    /**
     * 添加Forest文件上传用的Mutlipart工厂
     *
     * @param multipartFactory Forest文件上传用的Mutlipart工厂，{@link ForestMultipartFactory}类实例
     */
    public void addMultipartFactory(ForestMultipartFactory multipartFactory) {
        multipartFactories.add(multipartFactory);
    }

    /**
     * 处理参数的过滤器
     *
     * @param parameter  方法参数-字符串模板解析对象，{@link MappingParameter}类实例
     * @param filterName 过滤器名称
     */
    public void processParameterFilter(MappingParameter parameter, String filterName) {
        if (StringUtils.isNotEmpty(filterName)) {
            String[] filterNameArray = filterName.split(",");
            for (String name : filterNameArray) {
                Filter filter = configuration.newFilterInstance(name);
                parameter.addFilter(filter);
            }
        }
    }


    /**
     * 获得最终的请求类型
     *
     * @param request 请求对象
     * @param args 调用本对象对应方法时传入的参数数组
     * @return 请求类型，{@link ForestRequestType}枚举实例
     */
    private ForestRequestType type(ForestRequest request, Object[] args) {
        String renderedType = typeTemplate.render(request, args);
        if (StringUtils.isBlank(renderedType)) {
            String typeFromName = methodNameItems[0];
            ForestRequestType type = ForestRequestType.findType(typeFromName);
            if (type != null) {
                return type;
            }
            return ForestRequestType.GET;
        }
        ForestRequestType type = ForestRequestType.findType(renderedType);
        if (type != null) {
            return type;
        }
        throw new ForestRuntimeException("Http request type \"" + renderedType + "\" is not be supported.");
    }

    /**
     * 创建请求
     *
     * @param args 调用本对象对应方法时传入的参数数组
     * @return Forest请求对象，{@link ForestRequest}类实例
     */
    private ForestRequest<?> makeRequest(final Object[] args) {
        initMethod();
        final MetaRequest baseMetaRequest = interfaceProxyHandler.getBaseMetaRequest();
        ForestURL baseURL = null;
        

        // createExecutor and initialize http instance
        final ForestRequest<T> request = new ForestRequest<>(configuration, this, args);
        final ForestRequestType type = type(request, args);

        final ForestQueryMap queries = new ForestQueryMap(request);
        if (baseUrlTemplate != null) {
            baseURL = baseUrlTemplate.render(request, args, queries);
        }
        if (urlTemplate == null) {
            throw new ForestRuntimeException("request URL is empty");
        }
        final ForestURL renderedURL = urlTemplate.render(request, args, queries);

        String baseContentEncoding = null;
        if (baseEncodeTemplate != null) {
            baseContentEncoding = baseEncodeTemplate.render(request, args);
        }
        String contentEncoding = null;
        if (encodeTemplate != null) {
            contentEncoding = encodeTemplate.render(request, args);
        }

        String baseContentType = null;
        if (baseContentTypeTemplate != null) {
            baseContentType = baseContentTypeTemplate.render(request, args);
        }
        String baseUserAgent = null;
        if (baseUserAgentTemplate != null) {
            baseUserAgent = baseUserAgentTemplate.render(request, args);
        }
        String charset = null;
        final String renderedCharset = charsetTemplate.render(request, args);
        if (StringUtils.isNotBlank(renderedCharset)) {
            charset = renderedCharset;
        } else if (baseCharsetTemplate != null) {
            charset = baseCharsetTemplate.render(request, args);
        }

        String responseEncoding = null;
        if (responseEncodingTemplate != null) {
            responseEncoding = responseEncodingTemplate.render(request, args);
        }

        String sslProtocol = null;
        final String renderedSslProtocol = sslProtocolTemplate.render(request, args);
        if (StringUtils.isNotBlank(renderedSslProtocol) && !"null".equals(renderedSslProtocol)) {
            sslProtocol = renderedSslProtocol;
        } else if (baseSslProtocolTemplate != null) {
            sslProtocol = baseSslProtocolTemplate.render(request, args);
        } else {
            sslProtocol = configuration.getSslProtocol();
        }

        String renderedContentType = null;
        if (contentTypeTemplate != null) {
            renderedContentType = contentTypeTemplate.render(request, args).trim();
        }
        String renderedUserAgent = null;
        if (userAgentTemplate != null) {
            renderedUserAgent = userAgentTemplate.render(request, args).trim();
        }
        final List<RequestNameValue> nameValueList = new ArrayList<>();
        final String[] headerArray = baseMetaRequest.getHeaders();
        MappingTemplate[] baseHeaders = null;
        if (headerArray != null && headerArray.length > 0) {
            baseHeaders = new MappingTemplate[headerArray.length];
            for (int j = 0; j < baseHeaders.length; j++) {
                final MappingTemplate header = new MappingTemplate(
                        MappingTemplate.METHOD_TEMPLATE,
                        BaseRequest.class, "headers",
                        this, headerArray[j], forestParameters);
                baseHeaders[j] = header;
            }
        }

        final AddressSource addressSource = configuration.getBaseAddressSource();
        ForestAddress address = configuration.getBaseAddress();

        if (baseURL != null) {
            renderedURL.setBaseURL(baseURL);
            if (address == null) {
                // 默认根地址
                address = DEFAULT_ADDRESS;
            }
        }
        if (address != null) {
            renderedURL.setAddress(address, false);
        }
/*
        addressURL = new ForestURLBuilder()
                .setScheme("http")
                .setHost("localhost")
                .build();
*/

        boolean autoRedirection = configuration.isAutoRedirection();

        String bodyTypeName = "text";
        if (bodyTypeTemplate != null) {
            bodyTypeName = bodyTypeTemplate.render(request, args);
        }
        ForestDataType bodyType = ForestDataType.findByName(bodyTypeName);


        request.url(renderedURL)
                .type(type)
                .bodyType(bodyType)
                .addAllQuery(queries)
                .charset(charset)
                .autoRedirects(autoRedirection)
                .setSslProtocol(sslProtocol)
                .setLogConfiguration(logConfiguration)
                .setAsyncMode(configuration.getAsyncMode())
                .setAsync(async);

        if (addressSource != null) {
            address = addressSource.getAddress(request);
            request.address(address);
        }

        if (StringUtils.isNotEmpty(responseEncoding)) {
            request.setResponseEncode(responseEncoding);
        }

        if (StringUtils.isNotEmpty(renderedContentType)) {
            request.setContentType(renderedContentType);
        }

        if (StringUtils.isNotEmpty(contentEncoding)) {
            request.setContentEncoding(contentEncoding);
        }

        if (StringUtils.isNotEmpty(renderedUserAgent)) {
            request.setUserAgent(renderedUserAgent);
        }


        for (final MappingTemplate headerTemplate : headerTemplateArray) {
            final String header = headerTemplate.render(request, args);
            final String[] headNameValue = header.split(":", 2);
            if (headNameValue.length > 0) {
                final String name = headNameValue[0].trim();
                final RequestNameValue nameValue = new RequestNameValue(name, TARGET_HEADER);
                if (headNameValue.length == 2) {
                    nameValue.setValue(headNameValue[1].trim());
                }
                request.addHeader(nameValue);
            }
        }

        if (timeout != null) {
            request.setTimeout(timeout);
        } else if (baseTimeout != null) {
            request.setTimeout(baseTimeout);
        } else if (configuration.getTimeout() != null) {
            request.setTimeout(configuration.getTimeout());
        }

        if (connectTimeout != null) {
            request.setConnectTimeout(connectTimeout);
        } else if (baseConnectTimeout != null) {
            request.setConnectTimeout(baseConnectTimeout);
        } else if (configuration.getConnectTimeout() != null) {
            request.setConnectTimeout(configuration.getConnectTimeout());
        }

        if (readTimeout != null) {
            request.setReadTimeout(readTimeout);
        } else if (baseReadTimeout != null) {
            request.setReadTimeout(baseReadTimeout);
        } else if (configuration.getReadTimeout() != null) {
            request.setReadTimeout(configuration.getReadTimeout());
        }

        if (retryCount != null) {
            request.setMaxRetryCount(retryCount);
        } else if (baseRetryCount != null) {
            request.setMaxRetryCount(baseRetryCount);
        } else if (configuration.getMaxRetryCount() != null) {
            request.setMaxRetryCount(configuration.getMaxRetryCount());
        }

        if (maxRetryInterval >= 0) {
            request.setMaxRetryInterval(maxRetryInterval);
        } else if (baseMaxRetryInterval != null) {
            request.setMaxRetryInterval(baseMaxRetryInterval);
        } else if (configuration.getMaxRetryInterval() >= 0) {
            request.setMaxRetryInterval(configuration.getMaxRetryInterval());
        }

        final Class globalRetryerClass = configuration.getRetryer();

        if (retryerClass != null && ForestRetryer.class.isAssignableFrom(retryerClass)) {
            request.setRetryer(retryerClass);
        } else if (baseRetryerClass != null && ForestRetryer.class.isAssignableFrom(baseRetryerClass)) {
            request.setRetryer(baseRetryerClass);
        } else if (globalRetryerClass != null && ForestRetryer.class.isAssignableFrom(globalRetryerClass)) {
            request.setRetryer(globalRetryerClass);
        }

        for (int i = 0; i < namedParameters.size(); i++) {
            final MappingParameter parameter = namedParameters.get(i);
            if (parameter.isObjectProperties()) {
                final int target = parameter.isUnknownTarget() ? type.getDefaultParamTarget() : parameter.getTarget();
                Object obj = args[parameter.getIndex()];
                if (obj == null && StringUtils.isNotEmpty(parameter.getDefaultValue())) {
                    obj = parameter.getConvertedDefaultValue(configuration.getJsonConverter());
                }
                if (parameter.isJsonParam()) {
                    String json = "";
                    if (obj != null) {
                        ForestJsonConverter jsonConverter = configuration.getJsonConverter();
                        obj = parameter.getFilterChain().doFilter(configuration, obj);
                        json = jsonConverter.encodeToString(obj);
                    }
                    if (MappingParameter.isHeader(target)) {
                        request.addHeader(new RequestNameValue(parameter.getJsonParamName(), json, target)
                                .setDefaultValue(parameter.getDefaultValue()));
                    } else {
                        nameValueList.add(new RequestNameValue(parameter.getJsonParamName(), json, target, parameter.getPartContentType())
                                .setDefaultValue(parameter.getDefaultValue()));
                    }
                } else if (!parameter.getFilterChain().isEmpty()) {
                    obj = parameter.getFilterChain().doFilter(configuration, obj);
                    if (obj == null && StringUtils.isNotEmpty(parameter.getDefaultValue())) {
                        obj = parameter.getDefaultValue();
                    }
                    if (obj != null) {
                        if (MappingParameter.isHeader(target)) {
                            request.addHeader(new RequestNameValue(null, obj, target));
                        } else if (MappingParameter.isCookie(target)) {
                            request.addCookie(new RequestNameValue(null, obj, target));
                        } else if (MappingParameter.isQuery(target)) {
                            request.addQuery(obj.toString(), (Object) null,
                                    parameter.isUrlEncode(), parameter.getCharset());
                        } else if (MappingParameter.isBody(target)) {
                            ForestRequestBody body = RequestBodyBuilder
                                    .type(obj.getClass())
                                    .build(obj, parameter.getDefaultValue());
                            request.addBody(body);
                        } else {
                            nameValueList.add(new RequestNameValue(obj.toString(), target, parameter.getPartContentType())
                                    .setDefaultValue(parameter.getDefaultValue()));
                        }
                    }
                } else if (obj instanceof CharSequence) {
                    if (MappingParameter.isQuery(target)) {
                        request.addQuery(ForestQueryParameter.createSimpleQueryParameter(request.getQuery(), obj)
                                .setDefaultValue(parameter.getDefaultValue()));
                    } else if (MappingParameter.isBody(target)) {
                        request.addBody(new StringRequestBody(obj.toString())
                                .setDefaultValue(parameter.getDefaultValue()));
                    }
                } else if (obj instanceof Map) {
                    final Map map = (Map) obj;
                    if (MappingParameter.isQuery(target)) {
                        request.addQuery(map, parameter.isUrlEncode(), parameter.getCharset());
                    } else if (MappingParameter.isBody(target)) {
                        request.addBody(map, parameter.getPartContentType());
                    } else if (MappingParameter.isHeader(target)) {
                        request.addHeader(map);
                    } else if (MappingParameter.isCookie(target)) {
                        request.addCookie(map);
                    }
                } else if (obj instanceof Iterable
                        || (obj != null
                        && (obj.getClass().isArray()
                        || ReflectUtils.isPrimaryType(obj.getClass())))) {
                    if (MappingParameter.isQuery(target)) {
                        if (parameter.isJsonParam()) {
                            request.addQuery(parameter.getName(), obj,
                                    parameter.isUrlEncode(), parameter.getCharset());
                        } else {
                            if (obj instanceof Iterable) {
                                for (Object subItem : (Iterable) obj) {
                                    if (subItem instanceof SimpleQueryParameter) {
                                        request.addQuery((SimpleQueryParameter) subItem);
                                    } else {
                                        request.addQuery(ForestQueryParameter.createSimpleQueryParameter(
                                                request.getQuery(), subItem));
                                    }
                                }
                            } else if (obj.getClass().isArray()) {
                                if (obj instanceof SimpleQueryParameter[]) {
                                    request.addQuery((SimpleQueryParameter[]) obj);
                                }
                            }
                        }
                    } else if (MappingParameter.isBody(target)) {
                        final ForestRequestBody body = RequestBodyBuilder
                                .type(obj.getClass())
                                .build(obj, parameter.getDefaultValue());
                        request.addBody(body);
                    }
                } else if (MappingParameter.isBody(target)) {
                    final ForestRequestBody body = RequestBodyBuilder
                            .type(obj.getClass())
                            .build(obj, parameter.getDefaultValue());
                    request.addBody(body);
                } else {
                    try {
                        final List<RequestNameValue> list = getNameValueListFromObjectWithJSON(parameter, configuration, obj, type);
                        if (list != null) {
                            for (final RequestNameValue nameValue : list) {
                                if (nameValue.isInHeader()) {
                                    request.addHeader(nameValue);
                                } else {
                                    nameValueList.add(nameValue);
                                }
                            }
                        }
                    } catch (Throwable th) {
                        throw new ForestRuntimeException(th);
                    }
                }
            } else if (parameter.getIndex() != null) {
                final int target = parameter.isUnknownTarget() ? type.getDefaultParamTarget() : parameter.getTarget();
                final RequestNameValue nameValue = new RequestNameValue(parameter.getName(), target, parameter.getPartContentType())
                        .setDefaultValue(parameter.getDefaultValue());
                Object obj = args[parameter.getIndex()];
                if (obj == null && StringUtils.isNotEmpty(nameValue.getDefaultValue())) {
                    obj = parameter.getConvertedDefaultValue(configuration.getJsonConverter());
                }
                if (obj != null) {
                    if (MappingParameter.isQuery(target) &&
                            obj.getClass().isArray() &&
                            !(obj instanceof byte[]) &&
                            !(obj instanceof Byte[])) {
                        int len = Array.getLength(obj);
                        for (int idx = 0; idx < len; idx++) {
                            Object arrayItem = Array.get(obj, idx);
                            SimpleQueryParameter queryParameter = new SimpleQueryParameter(
                                    request.getQuery(),
                                    parameter.getName(), arrayItem,
                                    parameter.isUrlEncode(), parameter.getCharset());
                            request.addQuery(queryParameter);
                        }
                    } else {
                        nameValue.setValue(obj);
                        if (MappingParameter.isHeader(target)) {
                            request.addHeader(nameValue);
                        } else if (MappingParameter.isCookie(target)) {
                            request.addCookie(nameValue);
                        } else if (MappingParameter.isQuery(target)) {
                            if (!parameter.isJsonParam() && obj instanceof Iterable) {
                                int index = 0;
                                MappingTemplate template = makeTemplate(parameter);
                                VariableScope parentScope = request;
                                for (Object subItem : (Iterable) obj) {
                                    SubVariableScope scope = new SubVariableScope(parentScope);
                                    scope.setVariable("_it", subItem);
                                    scope.setVariable("_index", index++);
                                    String name = template.render(scope, args);
                                    request.addQuery(
                                            name, subItem,
                                            parameter.isUrlEncode(), parameter.getCharset());
                                }
                                // 恢复parentScope, 防止栈溢出
//                                template.setVariableScope(parentScope);
                            } else if (parameter.isJsonParam()) {
                                request.addJSONQuery(parameter.getName(), obj);
                            } else {
                                request.addQuery(
                                        parameter.getName(), obj,
                                        parameter.isUrlEncode(), parameter.getCharset());
                            }
                        } else {
                            MappingTemplate template = makeTemplate(parameter);
                            if (obj instanceof Iterable && template.hasIterateVariable()) {
                                int index = 0;
                                VariableScope parentScope = request;
                                for (Object subItem : (Iterable) obj) {
                                    SubVariableScope scope = new SubVariableScope(parentScope);
                                    scope.setVariable("_it", subItem);
                                    scope.setVariable("_index", index++);
                                    String name = template.render(scope, args);
                                    nameValueList.add(
                                            new RequestNameValue(name, subItem, target, parameter.getPartContentType())
                                                    .setDefaultValue(parameter.getDefaultValue()));
                                }
                            } else {
                                nameValueList.add(nameValue);
                            }
                        }
                    }
                }
            }
        }

        if (request.getContentType() == null) {
            if (StringUtils.isNotEmpty(baseContentType)) {
                request.setContentType(baseContentType);
            }
        }

        if (request.getContentEncoding() == null) {
            if (StringUtils.isNotEmpty(baseContentEncoding)) {
                request.setContentEncoding(baseContentEncoding);
            }
        }

        if (request.getUserAgent() == null) {
            if (StringUtils.isNotEmpty(baseUserAgent)) {
                request.setUserAgent(baseUserAgent);
            }
        }

        final List<ForestMultipart> multiparts = new ArrayList<>(multipartFactories.size());
        final String contentType = request.getContentType();

        if (!multipartFactories.isEmpty()) {
            if (StringUtils.isBlank(contentType)) {
                final String boundary = StringUtils.generateBoundary();
                request.setContentType(ContentType.MULTIPART_FORM_DATA + "; boundary=" + boundary);
            } else if (ContentType.MULTIPART_FORM_DATA.equalsIgnoreCase(contentType)
                    && request.getBoundary() == null) {
                request.setBoundary(StringUtils.generateBoundary());
            }
        }

        for (final ForestMultipartFactory factory : multipartFactories) {
            final MappingTemplate nameTemplate = factory.getNameTemplate();
            final MappingTemplate fileNameTemplate = factory.getFileNameTemplate();
            final int index = factory.getIndex();
            final Object data = args[index];
            factory.addMultipart(request, nameTemplate, fileNameTemplate, data, multiparts, args);
        }
        request.setMultiparts(multiparts);
        // setup ssl keystore
        if (sslKeyStoreId != null) {
            SSLKeyStore sslKeyStore = null;
            final String keyStoreId = sslKeyStoreId.render(request, args);
            if (StringUtils.isNotEmpty(keyStoreId)) {
                sslKeyStore = configuration.getKeyStore(keyStoreId);
                request.setKeyStore(sslKeyStore);
            }
        }
        if (encoder != null) {
            request.setEncoder(encoder);
        }
        if (decoder != null) {
            request.setDecoder(decoder);
        }
        if (progressStep >= 0) {
            request.setProgressStep(progressStep);
        }
        if (configuration.getDefaultParameters() != null) {
            request.addNameValue(configuration.getDefaultParameters());
        }
        if (baseHeaders != null && baseHeaders.length > 0) {
            HeaderUtils.addHeaders(request, baseHeaders, args);
        }
        if (configuration.getDefaultHeaders() != null) {
            request.addHeaders(configuration.getDefaultHeaders());
        }

        final List<RequestNameValue> dataNameValueList = new ArrayList<>();
        renderedContentType = request.getContentType();
        if (renderedContentType == null || renderedContentType.equalsIgnoreCase(ContentType.APPLICATION_X_WWW_FORM_URLENCODED)) {
            for (final MappingTemplate dataTemplate : dataTemplateArray) {
                final String data = dataTemplate.render(request, args);
                final String[] paramArray = data.split("&");
                for (final String dataParam : paramArray) {
                    final String[] dataNameValue = dataParam.split("=", 2);
                    if (dataNameValue.length > 0) {
                        final String name = dataNameValue[0].trim();
                        final RequestNameValue nameValue = new RequestNameValue(name, type.getDefaultParamTarget());
                        if (dataNameValue.length == 2) {
                            nameValue.setValue(dataNameValue[1].trim());
                        }
                        nameValueList.add(nameValue);
                        dataNameValueList.add(nameValue);
                    }
                }
            }
        } else {
            for (final MappingTemplate dataTemplate : dataTemplateArray) {
                final String data = dataTemplate.render(request, args);
                request.addBody(data);
            }
        }
        request.addNameValue(nameValueList);


        if (onSuccessParameter != null) {
            OnSuccess<?> onSuccessCallback = (OnSuccess<?>) args[onSuccessParameter.getIndex()];
            request.setOnSuccess(onSuccessCallback);
        }
        if (onErrorParameter != null) {
            OnError onErrorCallback = (OnError) args[onErrorParameter.getIndex()];
            request.setOnError(onErrorCallback);
        }
        if (onRedirectionParameter != null) {
            OnRedirection onRedirectionCallback = (OnRedirection) args[onRedirectionParameter.getIndex()];
            request.setOnRedirection(onRedirectionCallback);
        }
        if (onProgressParameter != null) {
            OnProgress onProgressCallback = (OnProgress) args[onProgressParameter.getIndex()];
            request.setOnProgress(onProgressCallback);
        }

        if (onSaveCookieParameter != null) {
            OnSaveCookie onSaveCookieCallback = (OnSaveCookie) args[onSaveCookieParameter.getIndex()];
            request.setOnSaveCookie(onSaveCookieCallback);
        }

        if (onLoadCookieParameter != null) {
            OnLoadCookie onLoadCookieCallback = (OnLoadCookie) args[onLoadCookieParameter.getIndex()];
            request.setOnLoadCookie(onLoadCookieCallback);
        }

        String dataType = dataTypeTemplate.render(request, args);
        if (StringUtils.isEmpty(dataType)) {
            request.setDataType(ForestDataType.TEXT);
        } else {
            dataType = dataType.toUpperCase();
            final ForestDataType forestDataType = ForestDataType.findByName(dataType);
            request.setDataType(forestDataType);
        }

        if (interceptorAttributesList != null && interceptorAttributesList.size() > 0) {
            for (final InterceptorAttributes attributes : interceptorAttributesList) {
                final InterceptorAttributes newAttrs = attributes.clone();
                request.addInterceptorAttributes(newAttrs.getInterceptorClass(), newAttrs);
            }
            for (final InterceptorAttributes attributes : request.getInterceptorAttributes().values()) {
                request.getInterceptorAttributes(attributes.getInterceptorClass()).render(request, args);
            }
        }

        if (globalInterceptorList != null && globalInterceptorList.size() > 0) {
            for (final Interceptor item : globalInterceptorList) {
                request.addInterceptor(item);
            }
        }

        if (baseInterceptorList != null && baseInterceptorList.size() > 0) {
            for (final Interceptor item : baseInterceptorList) {
                request.addInterceptor(item);
            }
        }

        if (interceptorList != null && interceptorList.size() > 0) {
            for (final Interceptor item : interceptorList) {
                request.addInterceptor(item);
            }
        }
        return request;
    }


    private List<RequestNameValue> getNameValueListFromObjectWithJSON(final MappingParameter parameter, final ForestConfiguration configuration, final Object obj, final ForestRequestType type) {
        if (obj == null) {
            return null;
        }
        final Map<String, Object> propMap = ReflectUtils.convertObjectToMap(obj, configuration);
        final List<RequestNameValue> nameValueList = new ArrayList<>();
        for (final Map.Entry<String, Object> entry : propMap.entrySet()) {
            final String name = entry.getKey();
            final Object value = entry.getValue();
            if (value != null) {
                final RequestNameValue nameValue = new RequestNameValue(
                        name,
                        value,
                        parameter.isUnknownTarget() ? type.getDefaultParamTarget() : parameter.getTarget(),
                        parameter.getPartContentType());
                nameValueList.add(nameValue);
            }
        }
        return nameValueList;
    }

    /**
     * 调用方法
     *
     * @param args 调用本对象对应方法时传入的参数数组
     * @return 调用本对象对应方法结束后返回的值，任意类型的对象实例
     */
    public Object invoke(final Object[] args) {
        final ForestRequest<?> request = makeRequest(args);
        MethodLifeCycleHandler<T> lifeCycleHandler = null;
        request.setBackend(configuration.getBackend());
        Type rType = this.getReturnType();
        // 如果返回类型为ForestRequest，直接返回请求对象
        if (ForestRequest.class.isAssignableFrom(returnClass)
                || ForestSSEListener.class.isAssignableFrom(returnClass)) {
            final Type retType = getReturnType();
            if (retType instanceof ParameterizedType) {
                final ParameterizedType parameterizedType = (ParameterizedType) retType;
                final Type[] genTypes = parameterizedType.getActualTypeArguments();
                if (genTypes.length > 0) {
                    Type targetType = genTypes[0];
                    rType = targetType;
                } else {
                    rType = String.class;
                }
                if (rType instanceof WildcardType) {
                    final WildcardType wildcardType = (WildcardType) rType;
                    final Type[] bounds = wildcardType.getUpperBounds();
                    if (bounds.length > 0) {
                        rType = bounds[0];
                    } else {
                        rType = String.class;
                    }
                }
                Type successType = rType;
                if (onSuccessClassGenericType != null) {
                    successType = onSuccessClassGenericType;
                }
                lifeCycleHandler = new MethodLifeCycleHandler<>(
                        rType, successType);
                request.setLifeCycleHandler(lifeCycleHandler);
                lifeCycleHandler.handleInvokeMethod(request, this, args);
                return request;
            } else {
                lifeCycleHandler = new MethodLifeCycleHandler<>(
                        rType, onSuccessClassGenericType);
            }
            request.setLifeCycleHandler(lifeCycleHandler);
            lifeCycleHandler.handleInvokeMethod(request, this, args);

            if (ForestSSEListener.class.isAssignableFrom(returnClass)) {
                if (ForestSSE.class.equals(returnClass)) {
                    return request.sse();
                }
                return request.sse(returnClass);
            }

            return request;
        }

        lifeCycleHandler = new MethodLifeCycleHandler<>(
                rType, onSuccessClassGenericType);
        request.setLifeCycleHandler(lifeCycleHandler);
        lifeCycleHandler.handleInvokeMethod(request, this, args);
        return request.execute(request.getBackend(), lifeCycleHandler);
    }


    /**
     * 获取泛型类型
     *
     * @param genType 带泛型参数的类型，{@link Type}接口实例
     * @param index   泛型参数下标
     * @return 泛型参数中的类型，{@link Type}接口实例
     */
    private static Type getGenericClassOrType(final Type genType, final int index) {
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (params[index] instanceof ParameterizedType) {
            return params[index];
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return params[index];
    }

    /**
     * 设置方法返回值类型
     *
     * @param returnType 方法返回值类型，{@link Type}接口实例
     */
    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    /**
     * 获取方法返回值类型
     *
     * @return 方法返回值类型，{@link Type}接口实例
     */
    public Type getReturnType() {
        if (returnType == null) {
            returnType = method.getGenericReturnType();
        }
        return returnType;
    }

    /**
     * 获取指定方法返回值类型的参数
     *
     * @return {@link MappingParameter}实例
     */
    public MappingParameter getReturnTypeParameter() {
        return returnTypeParameter;
    }

    /**
     * 设置指定方法返回值类型的参数
     *
     * @param returnTypeParameter {@link MappingParameter}实例
     */
    public void setReturnTypeParameter(MappingParameter returnTypeParameter) {
        this.returnTypeParameter = returnTypeParameter;
    }

    /**
     * 获取请求结果类型
     *
     * @return 请求结果类型，{@link Type}接口实例
     */
    public Type getResultType() {
        final Type type = getReturnType();
        if (type == null) {
            return Void.class;
        }
        final Class clazz = ReflectUtils.toClass(type);
        if (ForestResponse.class.isAssignableFrom(clazz)) {
            if (type instanceof ParameterizedType) {
                final Type[] types = ((ParameterizedType) type).getActualTypeArguments();
                if (types.length > 0) {
                    return types[0];
                }
            }
        }
        return type;
    }
}