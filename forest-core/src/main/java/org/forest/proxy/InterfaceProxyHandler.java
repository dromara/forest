package org.forest.proxy;

import org.forest.annotation.BaseRequest;
import org.forest.annotation.BaseURL;
import org.forest.config.ForestConfiguration;
import org.forest.config.VariableScope;
import org.forest.mapping.MappingTemplate;
import org.forest.mapping.MappingVariable;
import org.forest.reflection.ForestMethod;
import org.forest.utils.URLUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
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

    private String baseURL;

    private String baseContentType;

    private MappingTemplate[] baseHeaders;

    private Class[] baseInterceptorClasses;

    private Integer baseTimeout;

    private Integer baseRetryCount;

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
                MappingTemplate template = new MappingTemplate(value.trim(), this);
                template.compile();
                baseURL = template.render(new Object[] {});
                if (!URLUtils.hasProtocol(baseURL)) {
                    baseURL = "http://" + baseURL;
                }
                baseURL = URLUtils.getValidBaseURL(baseURL);
            }
            if (annotation instanceof BaseRequest) {
                BaseRequest baseRequestAnn = (BaseRequest) annotation;
                baseContentType = baseRequestAnn.contentType();
                String [] headerArray = baseRequestAnn.headers();
                if (headerArray != null && headerArray.length > 0) {
                    baseHeaders = new MappingTemplate[headerArray.length];
                    for (int j = 0; j < baseHeaders.length; j++) {
                        MappingTemplate header = new MappingTemplate(headerArray[j], this);
                        baseHeaders[j] = header;
                    }
                }
                baseTimeout = baseRequestAnn.timeout();
                baseTimeout = baseTimeout == -1 ? null : baseTimeout;
                baseRetryCount = baseRequestAnn.retryCount();
                baseRetryCount = baseRetryCount == -1 ? null : baseRetryCount;
                baseInterceptorClasses = baseRequestAnn.interceptor();
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
        ForestMethod forestMethod = forestMethodMap.get(method);
        return forestMethod.invoke(args);
    }

    @Override
    public Object getVariableValue(String name) {
        return configuration.getVariableValue(name);
    }

    public String getBaseURL() {
        return baseURL;
    }

    public MappingTemplate[] getBaseHeaders() {
        return baseHeaders;
    }

    public Class[] getBaseInterceptorClasses() {
        return baseInterceptorClasses;
    }

    public Integer getBaseTimeout() {
        return baseTimeout;
    }

    public String getBaseContentType() {
        return baseContentType;
    }

    public Integer getBaseRetryCount() {
        return baseRetryCount;
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
