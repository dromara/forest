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
import com.dtflys.forest.logging.ForestLogHandler;
import com.dtflys.forest.logging.LogConfiguration;
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

    private LogConfiguration baseLogConfiguration;


    private List<Annotation> baseAnnotations = new LinkedList<>();


    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public InterfaceProxyHandler(ForestConfiguration configuration, ProxyFactory proxyFactory, Class<T> interfaceClass) {
        this.configuration = configuration;
        this.proxyFactory = proxyFactory;
        this.interfaceClass = interfaceClass;
        this.interceptorFactory = configuration.getInterceptorFactory();
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
            } else {
                BaseLifeCycle baseLifeCycle = annotation.annotationType().getAnnotation(BaseLifeCycle.class);
                MethodLifeCycle methodLifeCycle = annotation.annotationType().getAnnotation(MethodLifeCycle.class);
                if (baseLifeCycle != null || methodLifeCycle != null) {
                    if (baseLifeCycle != null) {
                        Class<? extends BaseAnnotationLifeCycle> interceptorClass = baseLifeCycle.value();
                        if (interceptorClass != null) {
                            BaseAnnotationLifeCycle baseInterceptor = interceptorFactory.getInterceptor(interceptorClass);
                            baseInterceptor.onProxyHandlerInitialized(this, annotation);
                        }
                    }
                    baseAnnotations.add(annotation);
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

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if ("toString".equals(methodName) && (args == null || args.length == 0)) {
            return "{Forest Proxy Object of " + interfaceClass.getName() + "}";
        }
        if ("equals".equals(methodName) && (args != null && args.length == 1)) {
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

    public LogConfiguration getBaseLogConfiguration() {
        return baseLogConfiguration;
    }

    public void setBaseLogConfiguration(LogConfiguration baseLogConfiguration) {
        this.baseLogConfiguration = baseLogConfiguration;
    }
}
