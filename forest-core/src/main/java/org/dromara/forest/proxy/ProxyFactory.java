package org.dromara.forest.proxy;

import org.dromara.forest.config.ForestConfiguration;

import java.lang.reflect.Proxy;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-03-25 18:17
 */
public class ProxyFactory<T> {

    private ForestConfiguration configuration;
    private Class<T> interfaceClass;

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
            InterfaceProxyHandler<T> interfaceProxyHandler = new InterfaceProxyHandler<T>(configuration, this, interfaceClass);
            instance = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass, ForestClientProxy.class}, interfaceProxyHandler);
            if (cacheEnabled) {
                configuration.getInstanceCache().put(interfaceClass, instance);
            }
            return instance;
        }
    }

}
