package org.dromara.forest.lifecycles.method;

import org.dromara.forest.annotation.SSLSocketFactoryBuilder;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.lifecycles.MethodAnnotationLifeCycle;
import org.dromara.forest.reflection.ForestMethod;

/**
 * Forest后端框架注解的生命周期类
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
public class SSLSocketFactoryBuilderLifeCycle implements MethodAnnotationLifeCycle<SSLSocketFactoryBuilder, Object> {

    private final static String PARAM_KEY_SSL_SOCKET_FACTORY_BUILDER = "__ssl_socket_factory_builder";

    @Override
    public void onMethodInitialized(ForestMethod method, SSLSocketFactoryBuilder annotation) {
        final Class<? extends org.dromara.forest.ssl.SSLSocketFactoryBuilder> clazz = annotation.value();
        final org.dromara.forest.ssl.SSLSocketFactoryBuilder sslSocketFactoryBuilder =
                method.getConfiguration().getForestObjectFactory().getObject(clazz);
        if (sslSocketFactoryBuilder != null) {
            method.setExtensionParameterValue(PARAM_KEY_SSL_SOCKET_FACTORY_BUILDER, sslSocketFactoryBuilder);
        }
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        final Object sslSocketFactoryBuilder = request.getMethod().getExtensionParameterValue(PARAM_KEY_SSL_SOCKET_FACTORY_BUILDER);
        if (sslSocketFactoryBuilder != null && sslSocketFactoryBuilder instanceof org.dromara.forest.ssl.SSLSocketFactoryBuilder) {
            request.sslSocketFactoryBuilder((org.dromara.forest.ssl.SSLSocketFactoryBuilder) sslSocketFactoryBuilder);
        }
    }


}
