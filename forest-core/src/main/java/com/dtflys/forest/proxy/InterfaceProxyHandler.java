package com.dtflys.forest.proxy;

import com.dtflys.forest.annotation.BaseLifeCycle;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.BaseURL;
import com.dtflys.forest.annotation.MethodLifeCycle;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.interceptor.InterceptorFactory;
import com.dtflys.forest.lifecycles.BaseAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.URLUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-05-04
 */
public class InterfaceProxyHandler<T> implements InvocationHandler, VariableScope {

    private ForestConfiguration configuration;

    private ProxyFactory proxyFactory;

    private Class<T> interfaceClass;

    private Map<Method, ForestMethod> forestMethodMap = new HashMap<Method, ForestMethod>();

    private MetaRequest baseMetaRequest = new MetaRequest();

    private InterceptorFactory interceptorFactory;

    private String baseURL;

    private String baseContentType;

    private String baseContentEncoding;

    private String baseUserAgent;

    private String baseCharset;

    private Class[] baseInterceptorClasses;

    private List<Annotation> baseAnnotations = new LinkedList<>();

    private Integer baseTimeout;

    private Class baseRetryerClass;

    private Integer baseRetryCount;

    private Long baseMaxRetryInterval;

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public InterfaceProxyHandler(ForestConfiguration configuration, ProxyFactory proxyFactory, Class<T> interfaceClass) {
        this.configuration = configuration;
        this.proxyFactory = proxyFactory;
        this.interfaceClass = interfaceClass;
        prepareBaseInfo();
        initMethods();
    }


    private void prepareBaseInfo() {
        Annotation[] annotations = interfaceClass.getAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Annotation annotation = annotations[i];
            if (annotation instanceof BaseURL) {
                BaseURL baseURLAnn = (BaseURL) annotation;
                String value = baseURLAnn.value();
                if (value == null || value.trim().length() == 0) {
                    continue;
                }
                baseURL = value.trim();
                baseMetaRequest.setUrl(baseURL);
            }
            if (annotation instanceof BaseRequest) {
                BaseRequest baseRequestAnn = (BaseRequest) annotation;


                String baseURLValue = baseRequestAnn.baseURL();
                if (StringUtils.isNotBlank(baseURLValue)) {
                    baseURL = baseURLValue.trim();
                    baseMetaRequest.setUrl(baseURL);
                }

                baseContentType = baseRequestAnn.contentType();
                baseContentEncoding = baseRequestAnn.contentEncoding();
                baseUserAgent = baseRequestAnn.userAgent();
                baseCharset = baseRequestAnn.charset();

                baseMetaRequest.setContentType(baseContentType);
                baseMetaRequest.setContentEncoding(baseContentEncoding);
                baseMetaRequest.setUserAgent(baseUserAgent);
                baseMetaRequest.setCharset(baseCharset);

                String [] headerArray = baseRequestAnn.headers();

                baseTimeout = baseRequestAnn.timeout();
                baseTimeout = baseTimeout == -1 ? null : baseTimeout;
                baseRetryerClass = baseRequestAnn.retryer();
                baseRetryCount = baseRequestAnn.retryCount();
                baseRetryCount = baseRetryCount == -1 ? null : baseRetryCount;
                baseMaxRetryInterval = baseRequestAnn.maxRetryInterval();

                if (headerArray != null) {
                    baseMetaRequest.setHeaders(headerArray);
                }

                if (baseTimeout != null) {
                    baseMetaRequest.setTimeout(baseTimeout);
                }
                baseMetaRequest.setRetryer(baseRetryerClass);
                if (baseRetryCount != null) {
                    baseMetaRequest.setRetryCount(baseRetryCount);
                }
                if (baseMaxRetryInterval != null) {
                    baseMetaRequest.setMaxRetryInterval(baseMaxRetryInterval);
                }

                if (baseMaxRetryInterval < 0) {
                    baseMaxRetryInterval = null;
                }
                baseInterceptorClasses = baseRequestAnn.interceptor();
                baseMetaRequest.setInterceptor(baseInterceptorClasses);

            } else {
//                MethodLifeCycle icAnn = annotation.annotationType().getAnnotation(MethodLifeCycle.class);
//                if (icAnn != null) {
//                    baseAnnotations.add(annotation);
//                }
                BaseLifeCycle baseLifeCycle = annotation.annotationType().getAnnotation(BaseLifeCycle.class);
                if (baseLifeCycle != null) {
                    Class<? extends BaseAnnotationLifeCycle> interceptorClass = baseLifeCycle.value();
                    if (interceptorClass != null) {
                        BaseAnnotationLifeCycle baseInterceptor = interceptorFactory.getInterceptor(interceptorClass);
                        baseInterceptor.onProxyHandlerInitialized(this, annotation);
                        baseAnnotations.add(annotation);
                    }
                }
            }
        }
    }


    private void initMethods() {
        Method[] methods = interfaceClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            ForestMethod forestMethod = new ForestMethod(this, configuration, method);
            forestMethodMap.put(method, forestMethod);
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
//        if (methodName.equals("getProxyHandler") && (args == null || args.length == 0)) {
//            return this;
//        }
        if (methodName.equals("toString") && (args == null || args.length == 0)) {
            return "{Forest Proxy Object of " + interfaceClass.getName() + "}";
        }
        if (methodName.equals("equals") && (args != null && args.length == 1)) {
            Object obj = args[0];
            if (Proxy.isProxyClass(obj.getClass())) {
                InvocationHandler h1 = Proxy.getInvocationHandler(proxy);
                InvocationHandler h2 = Proxy.getInvocationHandler(obj);
                return h1.equals(h2);
            }
            return false;
        }
        ForestMethod forestMethod = forestMethodMap.get(method);
        return forestMethod.invoke(args);
    }

    public MetaRequest getBaseMetaRequest() {
        return baseMetaRequest;
    }

    @Override
    public Object getVariableValue(String name) {
        return configuration.getVariableValue(name);
    }


    public List<Annotation> getBaseAnnotations() {
        return baseAnnotations;
    }

    @Override
    public MappingVariable getVariable(String name) {
        return null;
    }

    @Override
    public ForestConfiguration getConfiguration() {
        return configuration;
    }
}
