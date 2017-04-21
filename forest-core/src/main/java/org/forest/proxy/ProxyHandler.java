package org.forest.proxy;

import org.forest.config.ForestConfiguration;
import org.forest.reflection.ForestMethod;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-05-04
 */
public class ProxyHandler<T> implements InvocationHandler {

    private ForestConfiguration configuration;

    private ProxyFactory proxyFactory;

    private Class<T> interfaceClass;

    private Map<Method, ForestMethod> forestMethodMap = new HashMap<Method, ForestMethod>();

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public ProxyHandler(ForestConfiguration configuration, ProxyFactory proxyFactory, Class<T> interfaceClass) {
        this.configuration = configuration;
        this.proxyFactory = proxyFactory;
        this.interfaceClass = interfaceClass;
        initMethods();
    }


    private void initMethods() {
        Method[] methods = interfaceClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (forestMethodMap.containsKey(method)) {
                continue;
            }
            ForestMethod forestMethod = new ForestMethod(configuration, method);
            forestMethodMap.put(method, forestMethod);
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ForestMethod forstMethod = forestMethodMap.get(method);
        if (forstMethod == null) {
            forstMethod = new ForestMethod(configuration, method);
            forestMethodMap.put(method, forstMethod);
        }
        return forstMethod.invoke(args);
    }

}
