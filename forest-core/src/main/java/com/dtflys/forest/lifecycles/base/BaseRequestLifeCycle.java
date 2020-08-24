package com.dtflys.forest.lifecycles.base;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.lifecycles.BaseAnnotationLifeCycle;
import com.dtflys.forest.proxy.InterfaceProxyHandler;
import com.dtflys.forest.reflection.MetaRequest;
import com.dtflys.forest.utils.StringUtils;

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

        baseMetaRequest.setContentType(baseContentType);
        baseMetaRequest.setContentEncoding(baseContentEncoding);
        baseMetaRequest.setUserAgent(baseUserAgent);
        baseMetaRequest.setCharset(baseCharset);

        String [] headerArray = annotation.headers();

        Integer baseTimeout = annotation.timeout();
        baseTimeout = baseTimeout == -1 ? null : baseTimeout;
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
