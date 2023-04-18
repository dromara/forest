package org.dromara.forest.lifecycles.method;

import org.dromara.forest.annotation.Retry;
import org.dromara.forest.callback.RetryWhen;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.lifecycles.MethodAnnotationLifeCycle;
import org.dromara.forest.mapping.MappingTemplate;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.utils.StringUtil;

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
        if (StringUtil.isNotBlank(maxRetryCountStr)) {
            MappingTemplate maxRetryCountTemplate = method.makeTemplate(Retry.class, "maxRetryCount", maxRetryCountStr);
            try {
                Integer maxRetryCount = Integer.parseInt(maxRetryCountTemplate.render(args));
                request.maxRetryCount(maxRetryCount);
            } catch (Throwable ignored) {
            }
        }
        if (StringUtil.isNotBlank(maxRetryIntervalStr)) {
            try {
                MappingTemplate maxRetryIntervalTemplate = method.makeTemplate(Retry.class, "maxRetryInterval", maxRetryIntervalStr);
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
