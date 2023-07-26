package org.dromara.forest.lifecycles.method;

import org.dromara.forest.annotation.Success;
import org.dromara.forest.callback.SuccessWhen;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.lifecycles.MethodAnnotationLifeCycle;
import org.dromara.forest.reflection.ForestMethod;

/**
 * 重试注解的生命周期类
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
public class SuccessLifeCycle implements MethodAnnotationLifeCycle<Success, Object> {

    private final static String PARAM_KEY_SUCCESS = "__success";

    @Override
    public void onMethodInitialized(ForestMethod method, Success annotation) {
        method.setExtensionParameterValue(PARAM_KEY_SUCCESS, annotation);
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        final Success annotation = (Success) request.getMethod().getExtensionParameterValue(PARAM_KEY_SUCCESS);
        final Class<? extends SuccessWhen> conditionClass = annotation.condition();
        if (conditionClass != null && !SuccessWhen.class.equals(conditionClass)) {
            request.successWhen(conditionClass);
        }
    }


}
