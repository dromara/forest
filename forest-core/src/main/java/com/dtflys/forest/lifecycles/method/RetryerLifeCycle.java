package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.Retryer;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.retryer.ForestRetryer;

/**
 * 重试器注解的生命周期类
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
public class RetryerLifeCycle implements MethodAnnotationLifeCycle<Retryer, Object> {

    @Override
    public void onMethodInitialized(ForestMethod method, Retryer annotation) {
        method.setExtensionParameterValue("retryerAnnotation", annotation);
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        Retryer annotation = (Retryer) request.getMethod().getExtensionParameterValue("retryerAnnotation");
        Class<? extends ForestRetryer> clazz = annotation.value();
        request.setRetryer(clazz);
    }


}
