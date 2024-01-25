package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.SSLSocketFactoryBuilder;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;

/**
 * Forest后端框架注解的生命周期类
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
public class SSLSocketFactoryBuilderLifeCycle implements MethodAnnotationLifeCycle<SSLSocketFactoryBuilder> {

    private final static String PARAM_KEY_SSL_SOCKET_FACTORY_BUILDER = "__ssl_socket_factory_builder";

    @Override
    public void onMethodInitialized(ForestMethod method, SSLSocketFactoryBuilder annotation) {
        final Class<? extends com.dtflys.forest.ssl.SSLSocketFactoryBuilder> clazz = annotation.value();
        final com.dtflys.forest.ssl.SSLSocketFactoryBuilder sslSocketFactoryBuilder =
                method.config().getForestObjectFactory().getObject(clazz);
        if (sslSocketFactoryBuilder != null) {
            method.setExtensionParameterValue(PARAM_KEY_SSL_SOCKET_FACTORY_BUILDER, sslSocketFactoryBuilder);
        }
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        final Object sslSocketFactoryBuilder = request.getMethod().getExtensionParameterValue(PARAM_KEY_SSL_SOCKET_FACTORY_BUILDER);
        if (sslSocketFactoryBuilder != null && sslSocketFactoryBuilder instanceof com.dtflys.forest.ssl.SSLSocketFactoryBuilder) {
            request.sslSocketFactoryBuilder((com.dtflys.forest.ssl.SSLSocketFactoryBuilder) sslSocketFactoryBuilder);
        }
    }


}
