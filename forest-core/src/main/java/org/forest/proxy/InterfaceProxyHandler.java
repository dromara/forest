package org.forest.proxy;

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

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public InterfaceProxyHandler(ForestConfiguration configuration, ProxyFactory proxyFactory, Class<T> interfaceClass) {
        this.configuration = configuration;
        this.proxyFactory = proxyFactory;
        this.interfaceClass = interfaceClass;
        prepareBaseURL();
        initMethods();
    }


    private void prepareBaseURL() {
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
                if (!URLUtils.hasProtocal(baseURL)) {
                    baseURL = "http://" + baseURL;
                }
                baseURL = URLUtils.getValidBaseURL(baseURL);
            }
        }
    }


    private void initMethods() {
        Method[] methods = interfaceClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (forestMethodMap.containsKey(method)) {
                continue;
            }
            ForestMethod forestMethod = new ForestMethod(this, configuration, method);
            forestMethodMap.put(method, forestMethod);
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ForestMethod forestMethod = forestMethodMap.get(method);
        if (forestMethod == null) {
            forestMethod = new ForestMethod(this, configuration, method);
            forestMethodMap.put(method, forestMethod);
        }
        return forestMethod.invoke(args);
    }

    @Override
    public Object getVariableValue(String name) {
        return configuration.getVariableValue(name);
    }

    public String getBaseURL() {
        return baseURL;
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
