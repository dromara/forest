package org.dromara.forest.lifecycles.base;

import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.lifecycles.BaseAnnotationLifeCycle;
import org.dromara.forest.proxy.InterfaceProxyHandler;
import org.dromara.forest.reflection.MetaRequest;
import org.dromara.forest.utils.StringUtils;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-23 23:03
 */
public class BaseRequestLifeCycle implements BaseAnnotationLifeCycle<BaseRequest, Object> {

    @Override
    public void onProxyHandlerInitialized(InterfaceProxyHandler interfaceProxyHandler, BaseRequest annotation) {
        final MetaRequest baseMetaRequest = interfaceProxyHandler.getBaseMetaRequest();
        final String baseURLValue = annotation.baseURL();
        if (StringUtils.isNotBlank(baseURLValue)) {
            final String baseURL = baseURLValue.trim();
            baseMetaRequest.setUrl(baseURL);
        }

        final String baseContentType = annotation.contentType();
        final String baseContentEncoding = annotation.contentEncoding();
        final String baseUserAgent = annotation.userAgent();
        final String baseCharset = annotation.charset();
        final String baseSslProtocol = annotation.sslProtocol();

        baseMetaRequest.setContentType(baseContentType);
        baseMetaRequest.setContentEncoding(baseContentEncoding);
        baseMetaRequest.setUserAgent(baseUserAgent);
        baseMetaRequest.setCharset(baseCharset);
        baseMetaRequest.setSslProtocol(baseSslProtocol);

        final String[] headerArray = annotation.headers();
        final Integer baseTimeout = annotation.timeout() == -1 ? null : annotation.timeout();
        final Integer baseConnectTimeout = annotation.connectTimeout() == -1 ? null : annotation.connectTimeout();
        final Integer baseReadTimeout = annotation.readTimeout() == -1 ? null : annotation.readTimeout();

        final Class<?> baseRetryerClass = annotation.retryer();
        final Integer baseRetryCount = annotation.retryCount() == -1 ? null : annotation.retryCount();
        final Long baseMaxRetryInterval = annotation.maxRetryInterval();

        if (headerArray != null) {
            baseMetaRequest.setHeaders(headerArray);
        }

        if (baseTimeout != null) {
            baseMetaRequest.setTimeout(baseTimeout);
        }

        if (baseConnectTimeout != null) {
            baseMetaRequest.setConnectTimeout(baseConnectTimeout);
        }

        if (baseReadTimeout != null) {
            baseMetaRequest.setReadTimeout(baseReadTimeout);
        }

        baseMetaRequest.setRetryer(baseRetryerClass);
        if (baseRetryCount != null &&baseRetryCount >= 0) {
            baseMetaRequest.setRetryCount(baseRetryCount);
        }
        if (baseMaxRetryInterval != null && baseMaxRetryInterval >= 0) {
            baseMetaRequest.setMaxRetryInterval(baseMaxRetryInterval);
        }

        final Class<?>[] baseInterceptorClasses = annotation.interceptor();
        baseMetaRequest.setInterceptor(baseInterceptorClasses);

    }
}
