package org.dromara.forest.lifecycles.method;

import org.dromara.forest.annotation.Retry;
import org.dromara.forest.callback.RetryWhen;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.lifecycles.MethodAnnotationLifeCycle;
import org.dromara.forest.mapping.MappingTemplate;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.utils.StringUtils;

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
        final Class<? extends RetryWhen> conditionClass = annotation.condition();
        if (conditionClass != null && !conditionClass.isInterface()) {
            final RetryWhen retryWhen = method.getConfiguration().getForestObject(conditionClass);
            method.setExtensionParameterValue(PARAM_KEY_RETRY_WHEN, retryWhen);
        }
        method.setExtensionParameterValue(PARAM_KEY_RETRY, annotation);
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        final Retry annotation = (Retry) request.getMethod().getExtensionParameterValue(PARAM_KEY_RETRY);
        final Object retryWhen = request.getMethod().getExtensionParameterValue(PARAM_KEY_RETRY_WHEN);
        final String maxRetryCountStr = annotation.maxRetryCount();
        final String maxRetryIntervalStr = annotation.maxRetryInterval();
        if (StringUtils.isNotBlank(maxRetryCountStr)) {
            final MappingTemplate maxRetryCountTemplate = method.makeTemplate(Retry.class, "maxRetryCount", maxRetryCountStr);
            try {
                final Integer maxRetryCount = Integer.parseInt(maxRetryCountTemplate.render(args));
                request.maxRetryCount(maxRetryCount);
            } catch (Throwable ignored) {
            }
        }
        if (StringUtils.isNotBlank(maxRetryIntervalStr)) {
            try {
                final MappingTemplate maxRetryIntervalTemplate = method.makeTemplate(Retry.class, "maxRetryInterval", maxRetryIntervalStr);
                final Long maxRetryInterval = Long.parseLong(maxRetryIntervalTemplate.render(args));
                request.setMaxRetryInterval(maxRetryInterval);
            } catch (Throwable ignored) {
            }
        }
        if (retryWhen != null) {
            request.retryWhen((RetryWhen) retryWhen);
        }
    }


}
