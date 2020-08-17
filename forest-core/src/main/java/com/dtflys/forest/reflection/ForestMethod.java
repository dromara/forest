package com.dtflys.forest.reflection;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.exceptions.ForestInterceptorDefineException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.filter.Filter;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestType;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.interceptor.InterceptorAttributes;
import com.dtflys.forest.interceptor.InterceptorFactory;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.multipart.ForestMultipartFactory;
import com.dtflys.forest.proxy.InterfaceProxyHandler;
import com.dtflys.forest.retryer.Retryer;
import com.dtflys.forest.ssl.SSLKeyStore;
import com.dtflys.forest.utils.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * 通过代理调用的实际执行的方法对象
 * @author gongjun
 * @since 2016-05-03
 */
public class ForestMethod<T> implements VariableScope {

    private final InterfaceProxyHandler interfaceProxyHandler;
    private final ForestConfiguration configuration;
    private InterceptorFactory interceptorFactory;
    private final Method method;
    private String[] methodNameItems;
    private Class returnClass;
    private MetaRequest metaRequest;
    private MappingTemplate baseUrlTemplate;
    private MappingTemplate urlTemplate;
    private MappingTemplate typeTemplate;
    private MappingTemplate dataTypeTemplate;
    private Integer baseTimeout = null;
    private Integer timeout = null;
    private Class baseRetryerClass = null;
    private Integer baseRetryCount = null;
    private Long baseMaxRetryInterval;
    private Integer retryCount = null;
    private long maxRetryInterval;
    private MappingTemplate baseEncodeTemplate = null;
    private MappingTemplate encodeTemplate = null;
    private MappingTemplate charsetTemplate = null;
    private MappingTemplate baseContentTypeTemplate;
    private MappingTemplate baseCharsetTemplate;
    private MappingTemplate contentTypeTemplate;
    private long progressStep = -1;
    private ForestConverter decoder = null;
    private String sslKeyStoreId;
    private MappingTemplate[] dataTemplateArray;
    private MappingTemplate[] headerTemplateArray;
    private MappingParameter[] parameterTemplateArray;
    private List<MappingParameter> namedParameters = new ArrayList<>();
    private List<ForestMultipartFactory> multipartFactories = new ArrayList<>();
    private Map<String, MappingVariable> variables = new HashMap<String, MappingVariable>();
    private MappingParameter onSuccessParameter = null;
    private MappingParameter onErrorParameter = null;
    private MappingParameter onProgressParameter = null;
    private List<Interceptor> globalInterceptorList;
    private List<Interceptor> baseInterceptorList;
    private List<Interceptor> interceptorList;
    private List<InterceptorAttributes> interceptorAttributesList;
    private Type onSuccessClassGenericType = null;
    private Class retryerClass = null;
    private boolean async = false;
    private boolean logEnable = true;

    public ForestMethod(InterfaceProxyHandler interfaceProxyHandler, ForestConfiguration configuration, Method method) {
        this.interfaceProxyHandler = interfaceProxyHandler;
        this.configuration = configuration;
        this.method = method;
        this.interceptorFactory = configuration.getInterceptorFactory();
        this.methodNameItems = NameUtils.splitCamelName(method.getName());
        processBaseProperties();
        processInterfaceMethods();
    }

    public ForestConfiguration getConfiguration() {
        return configuration;
    }


    public Object getVariableValue(String name) {
        Object value = configuration.getVariableValue(name);
        return value;
    }

    private MappingTemplate makeTemplate(String text) {
        return new MappingTemplate(text, this);
    }


    public Class getReturnClass() {
        return returnClass;
    }


    public MappingVariable getVariable(String name) {
        return variables.get(name);
    }

    private void processBaseProperties() {
        String baseUrl = interfaceProxyHandler.getBaseURL();
        if (StringUtils.isNotBlank(baseUrl)) {
            baseUrlTemplate = makeTemplate(baseUrl);
        }
        String baseContentEncoding = interfaceProxyHandler.getBaseContentEncoding();
        if (StringUtils.isNotBlank(baseContentEncoding)) {
            baseEncodeTemplate = makeTemplate(baseContentEncoding);
        }
        String baseContentType = interfaceProxyHandler.getBaseContentType();
        if (StringUtils.isNotBlank(baseContentType)) {
            baseContentTypeTemplate = makeTemplate(baseContentType);
        }
        String baseCharset = interfaceProxyHandler.getBaseCharset();
        if (StringUtils.isNotBlank(baseCharset)) {
            baseCharsetTemplate = makeTemplate(baseCharset);
        }
        baseTimeout = interfaceProxyHandler.getBaseTimeout();
        baseRetryerClass = interfaceProxyHandler.getBaseRetryerClass();
        baseRetryCount = interfaceProxyHandler.getBaseRetryCount();
        baseMaxRetryInterval = interfaceProxyHandler.getBaseMaxRetryInterval();

        List<Class> globalInterceptorClasses = configuration.getInterceptors();
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

        Class[] baseInterceptorClasses = interfaceProxyHandler.getBaseInterceptorClasses();
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

        List<Annotation> baseAnnotationList = interfaceProxyHandler.getBaseAnnotations();
        for (Annotation annotation : baseAnnotationList) {
            addMetaRequestAnnotation(annotation);
        }
    }

    private Interceptor addInterceptor(Class interceptorClass) {
        if (interceptorList == null) {
            interceptorList = new LinkedList<>();
        }
        if (!Interceptor.class.isAssignableFrom(interceptorClass) || interceptorClass.isInterface()) {
            throw new ForestRuntimeException("Class [" + interceptorClass.getName() + "] is not a implement of [" +
                    Interceptor.class.getName() + "] interface.");
        }
        Interceptor interceptor = interceptorFactory.getInterceptor(interceptorClass);
        interceptorList.add(interceptor);
        return interceptor;
    }

    /**
     * 添加元请求注释
     * @param annotation
     */
    private void addMetaRequestAnnotation(Annotation annotation) {
        Class<? extends Annotation> annType = annotation.annotationType();
        LifeCycle icClass = annType.getAnnotation(LifeCycle.class);
        if (icClass != null) {
            Class<? extends AnnotationLifeCycle> interceptorClass = icClass.value();
            if (!Interceptor.class.isAssignableFrom(interceptorClass)) {
                throw new ForestInterceptorDefineException(interceptorClass);
            }

            RequestAttributes requestAttributesAnn = annType.getAnnotation(RequestAttributes.class);
            if (requestAttributesAnn != null) {
                Map<String, Object> attrTemplates = ReflectUtils.getAttributesFromAnnotation(annotation);
                for (String key : attrTemplates.keySet()) {
                    Object value = attrTemplates.get(key);
                    if (value instanceof CharSequence) {
                        MappingTemplate template = makeTemplate(value.toString());
                        attrTemplates.put(key, template);
                    }
                }

                InterceptorAttributes attributes = new InterceptorAttributes(interceptorClass, attrTemplates);
                if (interceptorAttributesList == null) {
                    interceptorAttributesList = new LinkedList<>();
                }
                interceptorAttributesList.add(attributes);
            }
            Interceptor interceptor = addInterceptor(interceptorClass);
            if (interceptor instanceof AnnotationLifeCycle) {
                AnnotationLifeCycle lifeCycle = (AnnotationLifeCycle) interceptor;
                MetaRequest metaReq = lifeCycle.buildMetaRequest(annotation);

                if (metaReq != null) {
                    if (metaRequest != null) {
                        throw new ForestRuntimeException("[Forest] annotation \""
                                + annType.getName() + "\" can not be added on method \""
                                + method.getName() + "\", because a similar annotation \""
                                + metaRequest.getRequestAnnotation().annotationType().getName() + "\" has already been attached to this method.");
                    }
                    metaRequest = metaReq;
                    processMetaRequest(metaRequest);
                }
            }
        }
    }


    /**
     * 处理接口中定义的方法
     */
    private void processInterfaceMethods() {
        Annotation[] annotations = method.getAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Annotation ann = annotations[i];
            // 添加自定义注解
            addMetaRequestAnnotation(ann);
        }
        returnClass = method.getReturnType();
    }

    private void processMetaRequest(MetaRequest metaRequest) {
        Class[] paramTypes = method.getParameterTypes();
        Type[] genericParamTypes = method.getGenericParameterTypes();
        TypeVariable<Method>[] typeVariables = method.getTypeParameters();
        Annotation[][] paramAnns = method.getParameterAnnotations();
        Parameter[] parameters = method.getParameters();

        urlTemplate = makeTemplate(metaRequest.getUrl());
        typeTemplate = makeTemplate(metaRequest.getType());
        dataTypeTemplate = makeTemplate(metaRequest.getDataType());
        contentTypeTemplate = makeTemplate(metaRequest.getContentType());
        sslKeyStoreId = metaRequest.getKeyStore();
        encodeTemplate = makeTemplate(metaRequest.getContentEncoding());
        charsetTemplate = makeTemplate(metaRequest.getCharset());
        progressStep = metaRequest.getProgressStep();
        async = metaRequest.isAsync();
        retryerClass = metaRequest.getRetryer();
        Class decoderClass = metaRequest.getDecoder();
        String[] dataArray = metaRequest.getData();
        String[] headerArray = metaRequest.getHeaders();
        int tout = metaRequest.getTimeout();
        if (tout > 0) {
            timeout = tout;
        }
        int rtnum = metaRequest.getRetryCount();
        if (rtnum > 0) {
            retryCount = rtnum;
        }
        maxRetryInterval = metaRequest.getMaxRetryInterval();
        logEnable = configuration.isLogEnabled();
        if (!logEnable) {
            logEnable = metaRequest.isLogEnabled();
        }

        for (TypeVariable<Method> typeVariable : typeVariables) {
            System.out.println(typeVariable.getName());
        }

        parameterTemplateArray = new MappingParameter[paramTypes.length];
        processParameters(parameters, genericParamTypes, paramAnns);

        dataTemplateArray = new MappingTemplate[dataArray.length];
        for (int j = 0; j < dataArray.length; j++) {
            String data = dataArray[j];
            MappingTemplate dataTemplate = makeTemplate(data);
            dataTemplateArray[j] = dataTemplate;
        }

        headerTemplateArray = new MappingTemplate[headerArray.length];
        for (int j = 0; j < headerArray.length; j++) {
            String header = headerArray[j];
            MappingTemplate headerTemplate = makeTemplate(header);
            headerTemplateArray[j] = headerTemplate;
        }

        Class[] interceptorClasses = metaRequest.getInterceptor();
        if (interceptorClasses != null && interceptorClasses.length > 0) {
            for (int cidx = 0, len = interceptorClasses.length; cidx < len; cidx++) {
                Class interceptorClass = interceptorClasses[cidx];
                addInterceptor(interceptorClass);
            }
        }


        if (decoderClass != null && ForestConverter.class.isAssignableFrom(decoderClass)) {
            try {
                this.decoder = (ForestConverter) decoderClass.newInstance();
            } catch (InstantiationException e) {
                throw new ForestRuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new ForestRuntimeException(e);
            }
        }

    }

    /**
     * 处理参数列表
     * @param parameters
     * @param genericParamTypes
     * @param paramAnns
     */
    private void processParameters(Parameter[] parameters, Type[] genericParamTypes, Annotation[][] paramAnns) {
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            Class paramType = param.getType();
            Annotation[] anns = paramAnns[i];
            MappingParameter parameter = new MappingParameter();
            parameter.setIndex(i);
            parameter.setName(param.getName());
            parameterTemplateArray[i] = parameter;
            if (OnSuccess.class.isAssignableFrom(paramType)) {
                onSuccessParameter = parameter;
                Type genType = genericParamTypes[i];
                onSuccessClassGenericType = getGenericClassOrType(genType, 0);
            } else if (OnError.class.isAssignableFrom(paramType)) {
                onErrorParameter = parameter;
            } else if (OnProgress.class.isAssignableFrom(paramType)) {
                onProgressParameter = parameter;
            }

            processParameterAnnotation(parameter, paramType, anns, i);
        }
    }

    /**
     * 处理参数的注解
     * @param parameter
     * @param paramType
     * @param anns
     */
    private void processParameterAnnotation(MappingParameter parameter, Class paramType, Annotation[] anns, int paramIndex) {
        for (int i = 0; i < anns.length; i++) {
            Annotation ann = anns[i];
            if (ann instanceof DataParam) {
                DataParam dataAnn = (DataParam) ann;
                String name = dataAnn.value();
                String filterName = dataAnn.filter();
                parameter.setName(name);
                processParameterFilter(parameter, filterName);
                namedParameters.add(parameter);
                MappingVariable variable = new MappingVariable(name, paramType);
                processParameterFilter(variable, filterName);
                variable.setIndex(paramIndex);
                variables.put(dataAnn.value(), variable);
            } else if (ann instanceof Query) {
                Query dataAnn = (Query) ann;
                String name = dataAnn.value();
                String filterName = dataAnn.filter();
                if (StringUtils.isNotEmpty(name)) {
                    parameter.setName(name);
                    MappingVariable variable = new MappingVariable(name, paramType);
                    processParameterFilter(variable, filterName);
                    variable.setIndex(paramIndex);
                    variables.put(dataAnn.value(), variable);
                    parameter.setObjectProperties(false);
                } else {
                    parameter.setObjectProperties(true);
                }
                processParameterFilter(parameter, filterName);
                parameter.setQuery(true);
                namedParameters.add(parameter);
            } else if (ann instanceof Body) {
                Body dataAnn = (Body) ann;
                String name = dataAnn.value();
                String filterName = dataAnn.filter();
                if (StringUtils.isNotEmpty(name)) {
                    parameter.setName(name);
                    MappingVariable variable = new MappingVariable(name, paramType);
                    processParameterFilter(variable, filterName);
                    variable.setIndex(paramIndex);
                    variables.put(dataAnn.value(), variable);
                    parameter.setObjectProperties(false);
                } else {
                    parameter.setObjectProperties(true);
                }
                processParameterFilter(parameter, filterName);
                parameter.setQuery(false);
                namedParameters.add(parameter);
            } else if (ann instanceof DataVariable) {
                DataVariable dataAnn = (DataVariable) ann;
                String name = dataAnn.value();
                if (StringUtils.isEmpty(name)) {
                    name = parameter.getName();
                }
                String filterName = dataAnn.filter();
                MappingVariable variable = new MappingVariable(name, paramType);
                processParameterFilter(variable, filterName);
                variable.setIndex(paramIndex);
                variables.put(name, variable);
            } else if (ann instanceof DataObject) {
                DataObject dataAnn = (DataObject) ann;
                String jsonParamName = dataAnn.jsonParam();
                String filterName = dataAnn.filter();
                boolean isJsonParam = StringUtils.isNotEmpty(jsonParamName);
                parameter.setObjectProperties(true);
                parameter.setJsonParam(isJsonParam);
                if (isJsonParam) {
                    parameter.setJsonParamName(jsonParamName);
                }
                processParameterFilter(parameter, filterName);
                namedParameters.add(parameter);
            } else if (ann instanceof DataFile) {
                DataFile dataAnn = (DataFile) ann;
                String name = dataAnn.value();
                String fileName = dataAnn.fileName();
                MappingTemplate nameTemplate = makeTemplate(name);
                MappingTemplate fileNameTemplate = makeTemplate(fileName);
                ForestMultipartFactory factory = ForestMultipartFactory.createFactory(
                        paramType, paramIndex, nameTemplate, fileNameTemplate, contentTypeTemplate);
                multipartFactories.add(factory);
            }
        }
    }

    private void processParameterFilter(MappingParameter parameter, String filterName) {
        if (StringUtils.isNotEmpty(filterName)) {
            String[] filterNameArray = filterName.split(",");
            for (String name : filterNameArray) {
                Filter filter = configuration.newFilterInstance(name);
                parameter.addFilter(filter);
            }
        }
    }

    private void setRetryerToRequest(Class retryerClass, ForestRequest request) {
        try {
            Constructor constructor = retryerClass.getConstructor(ForestRequest.class);
            Retryer retryer = (Retryer) constructor.newInstance(request);
            request.setRetryer(retryer);
        } catch (NoSuchMethodException e) {
            throw new ForestRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new ForestRuntimeException(e);
        } catch (InstantiationException e) {
            throw new ForestRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ForestRuntimeException(e);
        }

    }

    /**
     * 获得最终的请求类型
     * @param args
     * @return
     */
    private ForestRequestType type(Object[] args) {
        String renderedType = typeTemplate.render(args);
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
     * @param args
     * @return
     */
    private ForestRequest makeRequest(Object[] args) {
        String baseUrl = null;
        if (baseUrlTemplate != null) {
            baseUrl = baseUrlTemplate.render(args);
        }
        String renderedUrl = urlTemplate.render(args);
        ForestRequestType type = type(args);
        String baseEncode = null;
        if (baseEncodeTemplate != null) {
            baseEncode = baseEncodeTemplate.render(args);
        }
        String encode = encodeTemplate.render(args);
        if (StringUtils.isEmpty(encode)) {
            encode = baseEncode;
        }
        String baseContentType = null;
        if (baseContentTypeTemplate != null) {
            baseContentType = baseContentTypeTemplate.render(args);
        }

        String charset = null;
        String renderedCharset = charsetTemplate.render(args);
        if (StringUtils.isNotBlank(renderedCharset)) {
            charset = renderedCharset;
        } else if (baseCharsetTemplate != null) {
            charset = baseCharsetTemplate.render(args);
        } else if (StringUtils.isNotBlank(configuration.getCharset())) {
            charset = configuration.getCharset();
        } else {
            charset = "UTF-8";
        }

        String renderedContentType = contentTypeTemplate.render(args).trim();
        if (StringUtils.isEmpty(renderedContentType)) {
            renderedContentType = baseContentType;
        }
        String newUrl = "";
        List<RequestNameValue> nameValueList = new ArrayList<RequestNameValue>();
        List<Object> bodyList = new ArrayList<>();
        MappingTemplate[] baseHeaders = interfaceProxyHandler.getBaseHeaders();
        renderedUrl = URLUtils.getValidURL(baseUrl, renderedUrl);
        String query = "";
        String protocol = "";
        try {
            URL u = new URL(renderedUrl);
            query = u.getQuery();
            if (StringUtils.isNotEmpty(query)) {
                String[] params = query.split("&");
                StringBuilder queryBuilder = new StringBuilder();
                if (params.length > 0) {
                    queryBuilder.append("?");
                }
                for (int i = 0; i < params.length; i++) {
                    String p = params[i];
                    String[] nameValue = p.split("=");
                    String name = nameValue[0];
                    queryBuilder.append(name);
                    RequestNameValue requestNameValue = new RequestNameValue(name, true);
                    nameValueList.add(requestNameValue);
                    if (nameValue.length > 1) {
                        String value = nameValue[1];
                        queryBuilder.append("=");
                        queryBuilder.append(value);
                        requestNameValue.setValue(value);
                    }
                    if (i < params.length - 1) {
                        queryBuilder.append("&");
                    }
                }
            }
            protocol = u.getProtocol();
            int port = u.getPort();
            newUrl = protocol + "://" + u.getHost();
            if (port != 80 && port > -1) {
                newUrl += ":" + port;
            }
            String path = u.getPath();
            if (StringUtils.isNotEmpty(path)) {
                newUrl += path;
            }

        } catch (MalformedURLException e) {
            throw new ForestRuntimeException(e);
        }

        for (int i = 0; i < namedParameters.size(); i++) {
            MappingParameter parameter = namedParameters.get(i);
            boolean isQuery;
            if (parameter.isObjectProperties()) {
                isQuery = parameter.getQuery() == null ? false : parameter.getQuery();
                Object obj = args[parameter.getIndex()];
                if (parameter.isJsonParam()) {
                    String  json = "";
                    if (obj != null) {
                        ForestJsonConverter jsonConverter = configuration.getJsonConverter();
                        obj = parameter.getFilterChain().doFilter(configuration, obj);
                        json = jsonConverter.encodeToString(obj);
                    }
                    nameValueList.add(new RequestNameValue(parameter.getJsonParamName(), json, isQuery));
                }
                else if (!parameter.getFilterChain().isEmpty()) {
                    obj = parameter.getFilterChain().doFilter(configuration, obj);
                    nameValueList.add(new RequestNameValue(null, obj, isQuery));
                }
                else if (obj instanceof List
                        || obj.getClass().isArray()
                        || ReflectUtils.isPrimaryType(obj.getClass())) {
                    bodyList.add(obj);
                }
                else if (obj instanceof Map) {
                    Map map = (Map) obj;
                    for (Object key : map.keySet()) {
                        if (key instanceof CharSequence) {
                            Object value = map.get(key);
                            isQuery = parameter.getQuery() == null ? type.isDefaultParamInQuery() : parameter.getQuery();
                            nameValueList.add(new RequestNameValue(String.valueOf(key), value, isQuery));
                        }
                    }
                }
                else {
                    try {
                        List<RequestNameValue> list = getNameValueListFromObjectWithJSON(parameter, obj, type);
                        nameValueList.addAll(list);
                    } catch (Throwable th) {
                        throw new ForestRuntimeException(th);
                    }
                }
            }
            else if (parameter.getIndex() != null) {
                isQuery = parameter.getQuery() == null ? type.isDefaultParamInQuery() : parameter.getQuery();
                RequestNameValue nameValue = new RequestNameValue(parameter.getName(), isQuery);
                Object obj = args[parameter.getIndex()];
                if (obj != null) {
                    nameValue.setValue(String.valueOf(obj));
                    nameValueList.add(nameValue);
                }
            }
        }

        List<ForestMultipart> multiparts = new ArrayList<>(multipartFactories.size());

        for (int i = 0; i < multipartFactories.size(); i++) {
            ForestMultipartFactory factory = multipartFactories.get(i);
            MappingTemplate nameTemplate = factory.getNameTemplate();
            MappingTemplate fileNameTemplate = factory.getFileNameTemplate();
            MappingTemplate contentTypeTemplate = factory.getContentTypeTemplate();
            int index = factory.getIndex();
            String name = null;
            String fileName = null;
            String contentType = null;
            if (nameTemplate != null) {
                name = nameTemplate.render(args);
            }
            if (fileNameTemplate != null) {
                fileName = fileNameTemplate.render(args);
            }
            if (contentTypeTemplate != null) {
                contentType = contentTypeTemplate.render(args);
            }
            Object data = args[index];
            if (data == null) {
                continue;
            }
            ForestMultipart multipart = factory.create(name, fileName, data, contentType);
            multiparts.add(multipart);
        }

        // setup ssl keystore
        SSLKeyStore sslKeyStore = null;
        if (StringUtils.isNotEmpty(sslKeyStoreId)) {
            sslKeyStore = configuration.getKeyStore(sslKeyStoreId);
        }

        // createExecutor and initialize http instance
        ForestRequest<T> request = new ForestRequest(configuration);
        request.setProtocol(protocol)
                .setUrl(newUrl)
                .setType(type)
                .setKeyStore(sslKeyStore)
                .setContentEncoding(encode)
                .setCharset(charset)
                .setContentType(renderedContentType)
                .setArguments(args)
                .setLogEnable(logEnable)
                .setMultiparts(multiparts)
                .setAsync(async);


        if (decoder != null) {
            request.setDecoder(decoder);
        }
        if (progressStep >= 0) {
            request.setProgressStep(progressStep);
        }
        if (configuration.getDefaultParameters() != null) {
            request.addData(configuration.getDefaultParameters());
        }
        if (baseHeaders != null && baseHeaders.length > 0) {
            for (MappingTemplate baseHeader : baseHeaders) {
                String headerText = baseHeader.render(args);
                String[] headerNameValue = headerText.split(":");
                if (headerNameValue.length > 1) {
                    request.addHeader(headerNameValue[0].trim(), headerNameValue[1].trim());
                }
            }
        }
        if (configuration.getDefaultHeaders() != null) {
            request.addHeaders(configuration.getDefaultHeaders());
        }

        List<RequestNameValue> dataNameValueList = new ArrayList<>();
        StringBuilder bodyBuilder = new StringBuilder();
        for (int i = 0; i < dataTemplateArray.length; i++) {
            MappingTemplate dataTemplate = dataTemplateArray[i];
            String data = dataTemplate.render(args);
            bodyBuilder.append(data);
            if (i < dataTemplateArray.length - 1) {
                bodyBuilder.append("&");
            }
            String[] paramArray = data.split("&");
            for (int j = 0; j < paramArray.length; j++) {
                String dataParam = paramArray[j];
                String[] dataNameValue = dataParam.split("=");
                if (dataNameValue.length > 0) {
                    String name = dataNameValue[0].trim();
                    RequestNameValue nameValue = new RequestNameValue(name, type.isDefaultParamInQuery());
                    if (dataNameValue.length == 2) {
                        nameValue.setValue(dataNameValue[1].trim());
                    }
                    nameValueList.add(nameValue);
                    dataNameValueList.add(nameValue);
                }
            }
        }
        request.addData(nameValueList);
        request.setBodyList(bodyList);
        if (bodyBuilder.length() > 0) {
            String requestBody = bodyBuilder.toString();
            request.setRequestBody(requestBody);
        }

        for (int i = 0; i < headerTemplateArray.length; i++) {
            MappingTemplate headerTemplate = headerTemplateArray[i];
            String header = headerTemplate.render(args);
            String[] headNameValue = header.split(":");
            if (headNameValue.length > 0) {
                String name = headNameValue[0].trim();
                RequestNameValue nameValue = new RequestNameValue(name, false);
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

        if (retryCount != null) {
            request.setRetryCount(retryCount);
        } else if (baseRetryCount != null) {
            request.setRetryCount(baseRetryCount);
        } else if (configuration.getRetryCount() != null) {
            request.setRetryCount(configuration.getRetryCount());
        }

        if (maxRetryInterval >= 0) {
            request.setMaxRetryInterval(maxRetryInterval);
        } else if (baseMaxRetryInterval != null) {
            request.setMaxRetryInterval(baseMaxRetryInterval);
        } else if (configuration.getMaxRetryInterval() >= 0) {
            request.setMaxRetryInterval(configuration.getMaxRetryInterval());
        }

        Class globalRetryerClass = configuration.getRetryer();

        if (retryerClass != null && Retryer.class.isAssignableFrom(retryerClass)) {
            setRetryerToRequest(retryerClass, request);
        } else if (baseRetryerClass != null && Retryer.class.isAssignableFrom(baseRetryerClass)) {
            setRetryerToRequest(baseRetryerClass, request);
        } else if (globalRetryerClass != null && Retryer.class.isAssignableFrom(globalRetryerClass)) {
            setRetryerToRequest(globalRetryerClass, request);
        }

        if (onSuccessParameter != null) {
            OnSuccess<?> onSuccessCallback = (OnSuccess<?>) args[onSuccessParameter.getIndex()];
            request.setOnSuccess(onSuccessCallback);
        }
        if (onErrorParameter != null) {
            OnError onErrorCallback = (OnError) args[onErrorParameter.getIndex()];
            request.setOnError(onErrorCallback);
        }
        if (onProgressParameter != null) {
            OnProgress onProgressCallback = (OnProgress) args[onProgressParameter.getIndex()];
            request.setOnProgress(onProgressCallback);
        }

        String dataType = dataTypeTemplate.render(args);
        if (StringUtils.isEmpty(dataType)) {
            request.setDataType(ForestDataType.TEXT);
        }
        else {
            dataType = dataType.toUpperCase();
            ForestDataType forestDataType = ForestDataType.valueOf(dataType);
            request.setDataType(forestDataType);
        }

        if (interceptorAttributesList != null && interceptorAttributesList.size() > 0) {
            for (InterceptorAttributes attributes : interceptorAttributesList) {
                request.addInterceptorAttributes(attributes.getInterceptorClass(), attributes);
                request.getInterceptorAttributes(attributes.getInterceptorClass()).render(args);
            }
        }

        if (globalInterceptorList != null && globalInterceptorList.size() > 0) {
            for (Interceptor item : globalInterceptorList) {
                request.addInterceptor(item);
            }
        }

        if (baseInterceptorList != null && baseInterceptorList.size() > 0) {
            for (Interceptor item : baseInterceptorList) {
                request.addInterceptor(item);
            }
        }

        if (interceptorList != null && interceptorList.size() > 0) {
            for (Interceptor item : interceptorList) {
                request.addInterceptor(item);
            }
        }
        return request;
    }



    /**
     * 从对象中获取键值对列表
     * @param obj
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private List<RequestNameValue> getNameValueListFromObject(Object obj, ForestRequestType type) throws InvocationTargetException, IllegalAccessException {
        Class clazz = obj.getClass();
        if (clazz.equals(Object.class)) {
            return new ArrayList<>();
        }

        Method[] methods = clazz.getDeclaredMethods();
        List<RequestNameValue> nameValueList = new ArrayList<>();
        for (int i = 0; i < methods.length; i++) {
            Method mtd = methods[i];
            String getterName = StringUtils.getGetterName(mtd);
            if (getterName == null) {
                continue;
            }
            Method getter = mtd;
            Object value = getter.invoke(obj);
            if (value != null) {
                RequestNameValue nameValue = new RequestNameValue(getterName ,value, type.isDefaultParamInQuery());
                nameValueList.add(nameValue);
            }

        }
        return nameValueList;
    }


    private List<RequestNameValue> getNameValueListFromObjectWithJSON(MappingParameter parameter, Object obj, ForestRequestType type) {
        Map<String, Object> propMap = configuration.getJsonConverter().convertObjectToMap(obj);
        boolean isQuery = parameter.getQuery() == null ? type.isDefaultParamInQuery() : parameter.getQuery();
        List<RequestNameValue> nameValueList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                RequestNameValue nameValue = new RequestNameValue(name ,value, isQuery);
                nameValueList.add(nameValue);
            }
        }
        return nameValueList;
    }

    /**
     * 调用方法
     * @param args
     * @return
     */
    public Object invoke(Object[] args) {
        ForestRequest request = makeRequest(args);
        MethodLifeCycleHandler<T> lifeCycleHandler = new MethodLifeCycleHandler<>(
                this, onSuccessClassGenericType);
        lifeCycleHandler.handleInvokeMethod(request, this, args);
        request.execute(configuration.getBackend(), lifeCycleHandler);
        return lifeCycleHandler.getResultData();
    }


    /**
     * 获取泛型类型
     * @param genType
     * @param index
     * @return
     */
    private static Type getGenericClassOrType(Type genType, final int index) {

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


    public Type getReturnType() {
        Type type = method.getGenericReturnType();
        return type;
    }


}
