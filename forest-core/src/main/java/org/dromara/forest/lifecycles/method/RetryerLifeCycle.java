package org.dromara.forest.lifecycles.method;

import org.dromara.forest.annotation.Retryer;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.lifecycles.MethodAnnotationLifeCycle;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.retryer.ForestRetryer;

/**
 * 重试器注解的生命周期类
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
public class RetryerLifeCycle implements MethodAnnotationLifeCycle<Retryer, Object> {

    private final static String PARAM_KEY_RETRYER = "__retryer";

    @Override
    public void onMethodInitialized(ForestMethod method, Retryer annotation) {
        method.setExtensionParameterValue(PARAM_KEY_RETRYER, annotation);
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        Retryer annotation = (Retryer) request.getMethod().getExtensionParameterValue(PARAM_KEY_RETRYER);
        Class<? extends ForestRetryer> clazz = annotation.value();
        request.setRetryer(clazz);
    }


}
