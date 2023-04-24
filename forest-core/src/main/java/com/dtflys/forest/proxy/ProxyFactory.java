package com.dtflys.forest.proxy;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.reflection.ForestMethod;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-03-25 18:17
 */
public class ProxyFactory<T> {

    private ForestConfiguration configuration;
    private Class<T> interfaceClass;

    private InterfaceProxyHandler<T> interfaceProxyHandler;

    public ProxyFactory(ForestConfiguration configuration, Class<T> interfaceClass) {
        this.configuration = configuration;
        this.interfaceClass = interfaceClass;
    }

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public List<ForestMethod> getForestMethods() {
        if (interfaceProxyHandler == null) {
            return new ArrayList<>();
        }
        return interfaceProxyHandler.getForestMethods();
    }

    public T createInstance() {
        T instance = (T) configuration.getInstanceCache().get(interfaceClass);
        boolean cacheEnabled = configuration.isCacheEnabled();
        if (cacheEnabled && instance != null) {
            return instance;
        }
        synchronized (configuration.getInstanceCache()) {
            instance = (T) configuration.getInstanceCache().get(interfaceClass);
            if (cacheEnabled && instance != null) {
                return instance;
            }
            interfaceProxyHandler = new InterfaceProxyHandler<T>(configuration, this, interfaceClass);
            instance = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass, ForestClientProxy.class}, interfaceProxyHandler);
            if (cacheEnabled) {
                configuration.getInstanceCache().put(interfaceClass, instance);
            }
            return instance;
        }
    }

}
