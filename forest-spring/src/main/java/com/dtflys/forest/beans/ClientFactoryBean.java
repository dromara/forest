package com.dtflys.forest.beans;

import com.dtflys.forest.config.ForestConfiguration;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-24 18:47
 */
public class ClientFactoryBean<T> implements FactoryBean<T> {

    private ForestConfiguration forestConfiguration;

    private Class<T> interfaceClass;

    public ForestConfiguration getForestConfiguration() {
        return forestConfiguration;
    }

    public void setForestConfiguration(ForestConfiguration forestConfiguration) {
        this.forestConfiguration = forestConfiguration;
    }

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public T getObject() throws Exception {
        return forestConfiguration.createInstance(interfaceClass);
    }

    public Class<?> getObjectType() {
        return interfaceClass;
    }

    public boolean isSingleton() {
        return true;
    }
}
