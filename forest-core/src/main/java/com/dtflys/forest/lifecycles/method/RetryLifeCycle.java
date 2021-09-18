package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.Retry;
import com.dtflys.forest.callback.RetryWhen;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.StringUtils;

/**
 * 重试注解的生命周期类
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
public class RetryLifeCycle implements MethodAnnotationLifeCycle<Retry, Object> {

    private final static String PARAM_KEY_RETRY_WHEN = "__retry_when";
    private final static String PARAM_KEY_RETRY = "__retry";

    @Override
    public void onMethodInitialized(ForestMethod method, Retry annotation) {
        Class<? extends RetryWhen> conditionClass = annotation.condition();
        if (conditionClass != null && !conditionClass.isInterface()) {
            RetryWhen retryWhen = method.getConfiguration().getForestObject(conditionClass);
            method.setExtensionParameterValue(PARAM_KEY_RETRY_WHEN, retryWhen);
        }
        method.setExtensionParameterValue(PARAM_KEY_RETRY, annotation);
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        Retry annotation = (Retry) request.getMethod().getExtensionParameterValue(PARAM_KEY_RETRY);
        Object retryWhen = request.getMethod().getExtensionParameterValue(PARAM_KEY_RETRY_WHEN);
        String maxRetryCountStr = annotation.maxRetryCount();
        String maxRetryIntervalStr = annotation.maxRetryInterval();
        if (StringUtils.isNotBlank(maxRetryCountStr)) {
            MappingTemplate maxRetryCountTemplate = method.makeTemplate(maxRetryCountStr);
            try {
                Integer maxRetryCount = Integer.parseInt(maxRetryCountTemplate.render(args));
                request.setRetryCount(maxRetryCount);
            } catch (Throwable ignored) {
            }
        }
        if (StringUtils.isNotBlank(maxRetryIntervalStr)) {
            try {
                MappingTemplate maxRetryIntervalTemplate = method.makeTemplate(maxRetryIntervalStr);
                Long maxRetryInterval = Long.parseLong(maxRetryIntervalTemplate.render(args));
                request.setMaxRetryInterval(maxRetryInterval);
            } catch (Throwable ignored) {
            }
        }
        if (retryWhen != null) {
            request.retryWhen((RetryWhen) retryWhen);
        }
    }


}
