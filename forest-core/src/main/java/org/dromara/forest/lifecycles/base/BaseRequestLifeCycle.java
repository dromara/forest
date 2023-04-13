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
        MetaRequest baseMetaRequest = interfaceProxyHandler.getBaseMetaRequest();
        String baseURLValue = annotation.baseURL();
        if (StringUtils.isNotBlank(baseURLValue)) {
            String baseURL = baseURLValue.trim();
            baseMetaRequest.setUrl(baseURL);
        }

        String baseContentType = annotation.contentType();
        String baseContentEncoding = annotation.contentEncoding();
        String baseUserAgent = annotation.userAgent();
        String baseCharset = annotation.charset();
        String baseSslProtocol = annotation.sslProtocol();

        baseMetaRequest.setContentType(baseContentType);
        baseMetaRequest.setContentEncoding(baseContentEncoding);
        baseMetaRequest.setUserAgent(baseUserAgent);
        baseMetaRequest.setCharset(baseCharset);
        baseMetaRequest.setSslProtocol(baseSslProtocol);

        String [] headerArray = annotation.headers();

        Integer baseTimeout = annotation.timeout();
        Integer baseConnectTimeout = annotation.connectTimeout();
        Integer baseReadTimeout = annotation.readTimeout();

        baseTimeout = baseTimeout == -1 ? null : baseTimeout;
        baseConnectTimeout = baseConnectTimeout == -1 ? null : baseConnectTimeout;
        baseReadTimeout = baseReadTimeout == -1 ? null : baseReadTimeout;

        Class baseRetryerClass = annotation.retryer();
        Integer baseRetryCount = annotation.retryCount();
        baseRetryCount = baseRetryCount == -1 ? null : baseRetryCount;
        Long baseMaxRetryInterval = annotation.maxRetryInterval();

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

        Class<?>[] baseInterceptorClasses = annotation.interceptor();
        baseMetaRequest.setInterceptor(baseInterceptorClasses);

    }
}
